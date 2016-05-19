package name.valery1707.megatel.sorm.api.auth;

import org.jetbrains.annotations.Contract;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.Nullable;
import java.util.Optional;

public class AccountUtils {
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
		return toUserDetails(currentAuthentication(securityContext));
	}

	/**
	 * @see AppUserDetailsService#loadUserByUsername(String)
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
