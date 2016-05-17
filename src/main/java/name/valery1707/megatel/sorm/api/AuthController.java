package name.valery1707.megatel.sorm.api;

import org.springframework.web.bind.annotation.RequestBody;
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

	@RequestMapping(method = RequestMethod.POST)
	public Map<String, Object> login(@RequestBody Map<String, String> login) {
		if (login != null && "admin".equals(login.get("username"))) {
			account = new HashMap<>();
			account.put("login", "super");
			account.put("name", "Super admin");
			account.put("permission", Arrays.asList("operator.task.list"));
			return account;
		} else {
			throw new IllegalStateException("Incorrect username");
		}
	}

	@RequestMapping(path = "logout", method = RequestMethod.POST)
	public void logout() {
		account = null;
	}

	@RequestMapping
	public Map<String, Object> current() {
		if (account == null) {
			throw new IllegalStateException("Not authorized");
		}
		return account;
	}
}
