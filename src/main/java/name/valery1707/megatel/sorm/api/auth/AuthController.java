package name.valery1707.megatel.sorm.api.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

	private Map<String, Object> toAccount(Authentication user) {
		if (user == null) {
			return null;
		}
		Map<String, Object> account = new HashMap<>();
		account.put("login", "super");
		account.put("name", "Super admin");
		account.put("permission", Arrays.asList("operator.task.list"));
		return account;
	}
}
