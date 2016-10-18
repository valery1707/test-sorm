package name.valery1707.core.app;

import name.valery1707.core.api.auth.AccountRepo;
import name.valery1707.core.domain.Account;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;

@Component
@Singleton
public class AppUserDetailsService implements UserDetailsService {

	@Inject
	private AccountRepo repo;

	@Inject
	private HttpServletRequest request;

	@Inject
	private LoginAttemptService loginAttemptService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		loginAttemptService.preventBruteForce(username, getClientIP(request));
		Account account = repo.getByUsernameAndIsActiveTrue(username);
		if (account == null) {
			throw new UsernameNotFoundException(String.format("Username '%s' not found", username));
		}
		return new AppUserDetails(account);
	}

	private String getClientIP(HttpServletRequest request) {
		String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader == null){
			return request.getRemoteAddr();
		}
		return xfHeader.split(",")[0];
	}
}
