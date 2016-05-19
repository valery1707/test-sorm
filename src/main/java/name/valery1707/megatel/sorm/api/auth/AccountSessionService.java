package name.valery1707.megatel.sorm.api.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import name.valery1707.megatel.sorm.domain.Account;
import name.valery1707.megatel.sorm.domain.AccountSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	private AccountRepo accountRepo;

	@Inject
	private AccountSessionRepo sessionRepo;

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
	public void login(AccountSession.Login mode, Authentication authentication) {
		WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
		Account account = accountRepo.getByUsernameAndIsActiveTrue(AccountUtils.toUserDetails(authentication).getUsername());//todo В этот момент пользователь уже может быть отключён
		AccountSession session = new AccountSession(account, mode, details.getSessionId(), toJSON(details));
		AccountSession save = sessionRepo.save(session);
		LOG.debug("login(mode: {}, user: {}, sessionId: {}) => {}", mode, authentication.getName(), details.getSessionId(), save.getId());
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
	public void logout(Authentication authentication, HttpSession session) {
		long current = System.currentTimeMillis();
		long accessedTime = session.getLastAccessedTime();
		boolean isTimeOut = (current - accessedTime) > (session.getMaxInactiveInterval() * 1000L);
		AccountSession.Logout mode = isTimeOut ? AccountSession.Logout.TIMEOUT : AccountSession.Logout.MANUAL;
		int count = sessionRepo.logout(session.getId(), ZonedDateTime.now(), mode);
		LOG.debug("logout(mode: {}, user: {}, sessionId: {}) => [{}]", mode, authentication != null ? authentication.getName() : null, session.getId(), count);
	}
}
