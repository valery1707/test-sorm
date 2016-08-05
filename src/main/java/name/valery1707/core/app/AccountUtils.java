package name.valery1707.core.app;

import org.jetbrains.annotations.Contract;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Nullable;
import java.util.Optional;

public class AccountUtils {
	public static Optional<Authentication> currentAuthentication() {
		return currentAuthentication(SecurityContextHolder.getContext());
	}

	public static Optional<Authentication> currentAuthentication(SecurityContext securityContext) {
		return Optional.ofNullable(securityContext.getAuthentication());
	}

	public static Optional<AppUserDetails> currentUserDetails() {
		return currentUserDetails(SecurityContextHolder.getContext());
	}

	public static Optional<AppUserDetails> currentUserDetails(SecurityContext securityContext) {
		return toUserDetails(currentAuthentication(securityContext));
	}

	/**
	 * @see AppUserDetailsService#loadUserByUsername(String)
	 */
	public static Optional<AppUserDetails> toUserDetails(Optional<Authentication> authentication) {
		return authentication.map(Authentication::getPrincipal)
				.filter(principal -> principal instanceof AppUserDetails)
				.map(principal -> (AppUserDetails) principal);
	}

	@Contract("null -> null;!null -> !null")
	public static AppUserDetails toUserDetails(@Nullable Authentication authentication) {
		return toUserDetails(Optional.ofNullable(authentication)).orElse(null);
	}
}
