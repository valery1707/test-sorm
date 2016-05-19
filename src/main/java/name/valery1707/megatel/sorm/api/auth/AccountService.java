package name.valery1707.megatel.sorm.api.auth;

import name.valery1707.megatel.sorm.domain.Account;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Optional;

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
}
