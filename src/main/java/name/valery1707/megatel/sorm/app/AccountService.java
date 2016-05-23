package name.valery1707.megatel.sorm.app;

import name.valery1707.megatel.sorm.domain.Account;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import java.util.*;

import static java.util.stream.Collectors.joining;
import static name.valery1707.megatel.sorm.app.AccountUtils.currentUserDetails;

@Service
@Scope(scopeName = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AccountService implements AuditorAware<Account> {

	private Optional<AppUserDetails> userDetails;
	private Optional<Account> account;
	private Collection<String> rights;

	@PostConstruct
	public void init() {
		userDetails = currentUserDetails();
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
		if (!account.isPresent()) {
			throw new AccessDeniedException("User is not logged in");
		}
	}

	public void requireAnyRole(Account.Role... roles) {
		requireAnyRole(Arrays.asList(roles));
	}

	public void requireAnyRole(List<Account.Role> roles) {
		requireAuthorized();
		//todo Нужно ли логировать заблокированные обращения к API?
		if (!roles.contains(account.get().getRole())) {
			throw new AccessDeniedException(String.format(
					"User '%s' does not have roles %s"
					, account.get().getUsername()
					, roles.stream()
							.map(Enum::name)
							.map(s -> "'" + s + "'")
							.collect(joining(", "))
			));
		}
	}

	public void requireAnyRight(String... rights) {
		requireAnyRight(Arrays.asList(rights));
	}

	public void requireAnyRight(List<String> rights) {
		requireAuthorized();
		for (String right : rights) {
			if (this.rights.contains(right)) {
				return;
			}
		}
		throw new AccessDeniedException(String.format(
				"User '%s' does not have roles %s"
				, account.get().getUsername()
				, rights.stream()
						.map(s -> "'" + s + "'")
						.collect(joining(", "))
		));
	}
}
