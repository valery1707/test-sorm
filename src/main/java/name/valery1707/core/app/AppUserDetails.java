package name.valery1707.core.app;

import name.valery1707.core.domain.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

public class AppUserDetails extends User {
	private final Account IAccount;

	public AppUserDetails(Account IAccount) {
		super(IAccount.getUsername(), IAccount.getPassword(), true, isActual(IAccount.getActiveUntil()), true, true, toAuthority(IAccount.getRole()));
		this.IAccount = IAccount;
	}

	public Account getAccount() {
		return IAccount;
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
		IAccount.setPassword(null);
	}

	private static Collection<? extends GrantedAuthority> toAuthority(Account.Role role) {
		return Collections.singleton(new SimpleGrantedAuthority(role.toString()));
	}

	private static boolean isActual(LocalDate future) {
		return
				future == null
				|| future.isAfter(LocalDate.now())
				;
	}
}
