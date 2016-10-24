package name.valery1707.core.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import name.valery1707.core.api.EventRepo;
import name.valery1707.core.api.auth.AccountRepo;
import name.valery1707.core.api.auth.AccountSessionRepo;
import name.valery1707.core.domain.Account;
import name.valery1707.core.domain.AccountSession;
import name.valery1707.core.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpSession;
import java.time.ZonedDateTime;

@Service
@Singleton
public class AccountSessionService {
	private static final Logger LOG = LoggerFactory.getLogger(AccountSessionService.class);

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private AccountSessionRepo sessionRepo;

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private AccountRepo accountRepo;

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private EventRepo eventRepo;

	@Inject
	private PasswordEncoder passwordEncoder;

	private ObjectMapper mapper;

	@PostConstruct
	public void init() {
		mapper = new ObjectMapper();
	}

	@Transactional
	public void serverRestart() {
		int count = sessionRepo.logoutAll(ZonedDateTime.now(), AccountSession.Logout.SERVER_RESTART);
		LOG.debug("serverRestart() => [{}]", count);
	}

	@Transactional
	@Async
	public void loginFail(String username, String password, String remoteAddress) {
		Event event = Event.server(Event.EventType.LOGIN, false, remoteAddress, null);
		Account account = accountRepo.getByUsernameAndIsActiveTrue(username);
		if (account == null) {
			//Неизвестный пользователь
			event.setExt("Неизвестный пользователь: " + username);
		} else {
			//Пользователь известен
			event.setCreatedBy(account);
			if (!passwordEncoder.matches(password, account.getPassword())) {
				//Ошибка в пароле
				event.setExt("Ошибка в пароле");
			} else {
				//Блокировка по IP
				event.setExt("Блокировка по IP: " + remoteAddress);
			}
		}
		eventRepo.save(event);
	}

	@Transactional
	public void login(AccountSession.Login mode, Authentication authentication) {
		SecurityContextHolder.getContext().setAuthentication(authentication);//todo Нужно ли откатывать назад текущее значение?
		WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
		AccountSession session = new AccountSession(mode, details.getSessionId(), toJSON(details));
		AccountSession save = sessionRepo.save(session);
		if (authentication.getPrincipal() instanceof AppUserDetails) {
			((AppUserDetails) authentication.getPrincipal()).setSession(save);
		}
		LOG.debug("login(mode: {}, user: {}, sessionId: {}) => {}", mode, authentication.getName(), details.getSessionId(), save.getId());
		eventRepo.save(Event.client(save.getAccount(), save, Event.EventType.LOGIN, true, details.getRemoteAddress(), null));
	}

	private String toJSON(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			LOG.warn("", e);
			return "{}";
		}
	}

	@Transactional
	public void changeSessionId(String oldSessionId, String newSessionId) {
		int count = sessionRepo.changeSessionId(oldSessionId, newSessionId);
		LOG.debug("changeSessionId({}, {}) => [{}]", oldSessionId, newSessionId, count);
	}

	@Transactional
	public void logout(@Nullable Authentication authentication, HttpSession session) {
		long current = System.currentTimeMillis();
		long accessedTime = session.getLastAccessedTime();
		boolean isTimeOut = (current - accessedTime) > (session.getMaxInactiveInterval() * 1000L);
		AccountSession.Logout mode = isTimeOut ? AccountSession.Logout.TIMEOUT : AccountSession.Logout.MANUAL;
		int count = sessionRepo.logout(session.getId(), ZonedDateTime.now(), mode);
		LOG.debug("logout(mode: {}, user: {}, sessionId: {}) => [{}]", mode, authentication != null ? authentication.getName() : null, session.getId(), count);
	}
}
