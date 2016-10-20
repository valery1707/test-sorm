package name.valery1707.core.app;

import name.valery1707.core.domain.Account;
import name.valery1707.core.domain.AccountSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

public class AppUserDetails extends User {
	private final Account account;
	private AccountSession session;

	public AppUserDetails(Account account) {
		super(account.getUsername(), account.getPassword(), true, isActual(account.getActiveUntil()), true, true, toAuthority(account.getRole()));
		this.account = account;
	}

	public Account getAccount() {
		return account;
	}

	public AccountSession getSession() {
		return session;
	}

	public void setSession(AccountSession session) {
		this.session = session;
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
		account.setPassword(null);
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
