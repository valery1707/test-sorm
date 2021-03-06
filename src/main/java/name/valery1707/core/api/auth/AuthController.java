package name.valery1707.core.api.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import javaslang.collection.LinkedHashMap;
import name.valery1707.core.app.AccountService;
import name.valery1707.core.app.AccountUtils;
import name.valery1707.core.app.AppUserDetails;
import name.valery1707.core.app.AppUserDetailsService;
import name.valery1707.core.domain.Account;
import name.valery1707.core.domain.Event.EventType;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private ZonedDateTime startAt = ZonedDateTime.now();

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

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private AccountRepo accountRepo;

	@Inject
	private AccountService accountService;

	@Inject
	private PasswordEncoder passwordEncoder;

	@RequestMapping(method = RequestMethod.PATCH)
	@Transactional(noRollbackFor = {AccessDeniedException.class})
	public Map<String, Object> changePassword(Authentication user, @RequestBody @Valid ChangePassword pass) {
		if (user == null) {
			throw new AccessDeniedException("User is not logged in");
		}
		EventType eventType = detectEventType((AppUserDetails) user.getPrincipal());
		Account account = accountRepo.getOne(AccountUtils.toUserDetails(user).getAccount().getId());
		if (!passwordEncoder.matches(pass.getOldPassword(), account.getPassword())) {
			accountService.logEventFail(eventType, "Old password is invalid");
			throw new AccessDeniedException("Old password is invalid");
		}
		accountRepo.setPasswordById(passwordEncoder.encode(pass.getNewPassword()), account.getId());
		accountService.logEventSuccess(eventType);
		return toAccount(user);
	}

	private EventType detectEventType(AppUserDetails user) {
		switch (user.getAccount().getRole()) {
			case ADMIN:
				return EventType.ADMIN_CHANGE_PASSWORD;
			case OPERATOR:
				return EventType.OPERATOR_CHANGE_PASSWORD;
			case SUPER:
				return null;
			case SUPERVISOR:
				return EventType.SUPERVISOR_CHANGE_PASSWORD;
			default:
				return null;
		}
	}

	public static class ChangePassword {
		@NotNull
		@Size(min = 3)
		@JsonProperty("old")
		private String oldPassword;
		@NotNull
		@Size(min = 3)
		@JsonProperty("new")
		private String newPassword;

		public String getOldPassword() {
			return oldPassword;
		}

		public void setOldPassword(String oldPassword) {
			this.oldPassword = oldPassword;
		}

		public String getNewPassword() {
			return newPassword;
		}

		public void setNewPassword(String newPassword) {
			this.newPassword = newPassword;
		}
	}

	//todo Вместо дублирования списка прав на стороне клиента их можно получать отсюда, но запрос выполняется позднее чем проверка прав
	@RequestMapping("/roles")
	public Map<String, Collection<String>> roles() {
		return Stream.of(Account.Role.values())
				.collect(toMap(
						Enum::name,
						Account.Role::getRights
				));
	}

	/**
	 * @see AppUserDetailsService#loadUserByUsername(java.lang.String)
	 */
	private Map<String, Object> toAccount(@Nullable Authentication user) {
		if (user == null) {
			return null;
		}
		AppUserDetails principal = (AppUserDetails) user.getPrincipal();
		Collection<String> rights = javaslang.collection.List.ofAll(principal.getAuthorities())
				.map(GrantedAuthority::getAuthority)
				.map(Account.Role::valueOf)
				.flatMap(Account.Role::getRights)
				.distinct()
				.toJavaList();

		Map<String, Object> account = new HashMap<>();
		account.put("login", user.getName());
		account.put("name", principal.getUsername());
		account.put("permission", rights);
		return account;
	}

	@RequestMapping(path = "csrf", method = RequestMethod.GET)
	public Map<String, Object> csrfCheck() {
		return LinkedHashMap.<String, Object>empty()
				.put("success", true)
				.put("startAt", startAt)
				.put("uptime", Duration.between(startAt, ZonedDateTime.now()).toString())
				.toJavaMap();
	}
}
