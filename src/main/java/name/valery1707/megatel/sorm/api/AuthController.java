package name.valery1707.megatel.sorm.api;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private Map<String, Object> account;

	@RequestMapping(headers = {HttpHeaders.AUTHORIZATION})
	public Map<String, Object> login(Authentication user) {
		if (user != null) {
			account = new HashMap<>();
			account.put("login", "super");
			account.put("name", "Super admin");
			account.put("permission", Arrays.asList("operator.task.list"));
		}
		return account;
	}

	@RequestMapping(path = "logout", method = RequestMethod.POST)
	public void logout() {
		account = null;
	}

	@RequestMapping
	public Map<String, Object> current() {
		//todo Inject Authentication
		return account;
	}
}
