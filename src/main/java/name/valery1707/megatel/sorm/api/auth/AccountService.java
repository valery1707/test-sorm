package name.valery1707.megatel.sorm.api.auth;

import name.valery1707.megatel.sorm.domain.Account;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;
import static name.valery1707.megatel.sorm.api.auth.AccountUtils.currentUserDetails;

@Service
@Scope(scopeName = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AccountService {

	@Inject
	private AccountRepo accountRepo;

	private Optional<UserDetails> userDetails;
	private Optional<Account> account;

	@PostConstruct
	public void init() {
		userDetails = currentUserDetails();
		account = userDetails.map(UserDetails::getUsername).map(accountRepo::getByUsernameAndIsActiveTrue);
	}

	public Optional<UserDetails> getUserDetails() {
		return userDetails;
	}

	public Optional<Account> getAccount() {
		return account;
	}

	public void hasAnyRole(Account.Role... roles) {
		hasAnyRole(Arrays.asList(roles));
	}

	public void hasAnyRole(List<Account.Role> roles) {
		//todo Нужно ли логировать заблокированные обращения к API?
		if (!account.isPresent()) {
			throw new AccessDeniedException("User is not logged in");
		}
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

	public void hasAnyRight(String... rights) {
		hasAnyRight(Arrays.asList(rights));
	}

	public void hasAnyRight(List<String> rights) {
		//todo Implement
	}
}
