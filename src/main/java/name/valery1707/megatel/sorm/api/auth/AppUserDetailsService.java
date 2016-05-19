package name.valery1707.megatel.sorm.api.auth;

import name.valery1707.megatel.sorm.domain.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

@Component
@Singleton
public class AppUserDetailsService implements UserDetailsService {

	@Inject
	private AccountRepo repo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = repo.getByUsernameAndIsActiveTrue(username);
		if (account == null) {
			throw new UsernameNotFoundException(String.format("Username '%s' not found", username));
		}
		return new User(account.getUsername(), account.getPassword(), true, isActual(account.getActiveUntil()), true, true, toAuthority(account.getRole()));
	}

	private Collection<? extends GrantedAuthority> toAuthority(Account.Role role) {
		return Collections.singleton(new SimpleGrantedAuthority(role.toString()));
	}

	private boolean isActual(LocalDate future) {
		return
				future == null
				|| future.isAfter(LocalDate.now())
				;
	}
}
