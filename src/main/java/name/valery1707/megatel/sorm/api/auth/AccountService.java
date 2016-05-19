package name.valery1707.megatel.sorm.api.auth;

import name.valery1707.megatel.sorm.domain.Account;
import org.jetbrains.annotations.Contract;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Optional;

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

	public static Optional<Authentication> currentAuthentication() {
		return currentAuthentication(SecurityContextHolder.getContext());
	}

	public static Optional<Authentication> currentAuthentication(SecurityContext securityContext) {
		return Optional.ofNullable(securityContext.getAuthentication());
	}

	public static Optional<UserDetails> currentUserDetails() {
		return currentUserDetails(SecurityContextHolder.getContext());
	}

	public static Optional<UserDetails> currentUserDetails(SecurityContext securityContext) {
		return AccountService.toUserDetails(currentAuthentication(securityContext));
	}

	/**
	 * @see AppUserDetailsService#loadUserByUsername(java.lang.String)
	 */
	public static Optional<UserDetails> toUserDetails(Optional<Authentication> authentication) {
		return authentication.map(Authentication::getPrincipal)
				.filter(principal -> principal instanceof UserDetails)
				.map(principal -> (UserDetails) principal);
	}

	@Contract("null -> null;!null -> !null")
	public static UserDetails toUserDetails(@Nullable Authentication authentication) {
		return toUserDetails(Optional.ofNullable(authentication)).orElse(null);
	}
}
