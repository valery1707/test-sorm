package name.valery1707.megatel.sorm.api.auth;

import name.valery1707.megatel.sorm.domain.Account;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	/**
	 * Spring сам автоматически определяет что запрос сюда это авторизация и логинит пользователя в сессию.
	 * <a href="https://spring.io/blog/2015/01/12/the-login-page-angular-js-and-spring-security-part-ii">Подробности тут</a>
	 */
	@RequestMapping(headers = {HttpHeaders.AUTHORIZATION})
	public Map<String, Object> login(Authentication user) {
		return toAccount(user);
	}

	@RequestMapping
	public Map<String, Object> current(Authentication user) {
		return toAccount(user);
	}

	/**
	 * @see AppUserDetailsService#loadUserByUsername(java.lang.String)
	 */
	private Map<String, Object> toAccount(@Nullable Authentication user) {
		if (user == null) {
			return null;
		}
		UserDetails principal = (UserDetails) user.getPrincipal();
		Collection<String> rights = principal.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.map(Account.Role::valueOf)
				.flatMap(Account.Role::getRightsStream)
				.distinct()
				.collect(Collectors.toList());

		Map<String, Object> account = new HashMap<>();
		account.put("login", user.getName());
		account.put("name", principal.getUsername());
		account.put("permission", rights);
		return account;
	}
}
