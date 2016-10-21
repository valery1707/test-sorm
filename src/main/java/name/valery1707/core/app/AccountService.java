package name.valery1707.core.app;

import javaslang.collection.List;
import javaslang.collection.Seq;
import name.valery1707.core.api.EventRepo;
import name.valery1707.core.domain.Account;
import name.valery1707.core.domain.Event;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@Scope(scopeName = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AccountService implements AuditorAware<Account> {

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private EventRepo eventRepo;

	private Optional<AppUserDetails> userDetails;
	private Optional<Account> account;
	private Collection<String> rights;

	@PostConstruct
	public void init() {
		userDetails = AccountUtils.currentUserDetails();
		account = userDetails.map(AppUserDetails::getAccount);
		rights = account
				.map(Account::getRole)
				.map(Account.Role::getRights)
				.orElse(Collections.emptyList());
	}

	public Optional<AppUserDetails> getUserDetails() {
		return userDetails;
	}

	public Optional<Account> getAccount() {
		return account;
	}

	@Override
	public Account getCurrentAuditor() {
		return getAccount().orElseGet(() -> {
					//Если аккаунт отсутствует в кеше, то пытаемся снова его получить
					init();
					return getAccount().orElse(null);
				}
		);
	}

	public void requireAuthorized() {
		if (!isAuthorized()) {
			throw new AccessDeniedException("User is not logged in");
		}
	}

	public boolean isAuthorized() {
		return account.isPresent();
	}

	public void requireAnyRole(Event.EventType type, Account.Role... roles) {
		requireAnyRole(type, Arrays.asList(roles));
	}

	public void requireAnyRole(Event.EventType type, Collection<Account.Role> roles) {
		requireAnyRole(type, List.ofAll(roles));
	}

	public void requireAnyRole(Event.EventType type, Seq<Account.Role> roles) {
		requireAuthorized();
		if (!hasAnyRole(roles)) {
			//todo Не будет ли откачена транзакция?
			logEventFail(type);
			throw new AccessDeniedException(String.format(
					"User '%s' does not have roles %s"
					, account.get().getUsername()
					, roles.mkString("'", "', '", "'")
			));
		}
	}

	public boolean hasAnyRole(Account.Role... roles) {
		return hasAnyRole(Arrays.asList(roles));
	}

	public boolean hasAnyRole(Collection<Account.Role> roles) {
		return hasAnyRole(List.ofAll(roles));
	}

	public boolean hasAnyRole(Seq<Account.Role> roles) {
		return isAuthorized() &&
			   roles.contains(account.get().getRole());
	}

	public void requireAnyRight(String... rights) {
		requireAnyRight(Arrays.asList(rights));
	}

	public void requireAnyRight(Collection<String> rights) {
		requireAuthorized();
		javaslang.collection.List<String> notFound = List.ofAll(rights)
				.filter(((Predicate<String>) this.rights::contains).negate());
		if (!notFound.isEmpty()) {
			throw new AccessDeniedException(String.format(
					"User '%s' does not have roles %s"
					, account.get().getUsername()
					, notFound.mkString("'", "', '", "'")
			));
		}
	}

	@Transactional
	public void logEventSuccess(Event.EventType type) {
		logEvent(type, true);
	}

	@Transactional
	public void logEventFail(Event.EventType type) {
		logEventFail(type, null);
	}

	@Transactional
	public void logEventFail(Event.EventType type, @Nullable String ext) {
		logEvent(type, false, ext);
	}

	@Transactional
	public void logEvent(Event.EventType type, boolean success) {
		logEvent(type, success, null);
	}

	@Transactional
	public void logEvent(@Nullable Event.EventType type, boolean success, @Nullable String ext) {
		if (type == null) {
			return;
		}
		WebAuthenticationDetails details = (WebAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
		Event event = Event.client(
				getCurrentAuditor(),
				getUserDetails().map(AppUserDetails::getSession).orElse(null),
				type,
				success,
				details.getRemoteAddress(),
				ext
		);
		eventRepo.save(event);
	}
}
