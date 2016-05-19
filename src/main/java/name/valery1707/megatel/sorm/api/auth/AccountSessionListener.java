package name.valery1707.megatel.sorm.api.auth;

import name.valery1707.megatel.sorm.domain.Account;
import name.valery1707.megatel.sorm.domain.AccountSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionFixationProtectionEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpSession;
import java.time.ZonedDateTime;
import java.util.Objects;

@Service
@Singleton
public class AccountSessionListener {
	private static final Logger LOG = LoggerFactory.getLogger(AccountSessionListener.class);

	@Inject
	private AccountRepo accountRepo;

	@Inject
	private AccountSessionRepo sessionRepo;

	/**
	 * Начало сессии работы с полной авторизацией
	 */
	@EventListener
	public void authenticationSuccessEvent(AuthenticationSuccessEvent event) {
		login(AccountSession.Login.MANUAL, event.getAuthentication());
	}

	/**
	 * Начало сессии работы с автоматической авторизацией (RememberMe)
	 */
	@EventListener
	public void interactiveAuthenticationSuccessEvent(InteractiveAuthenticationSuccessEvent event) {
		AccountSession.Login mode = RememberMeAuthenticationFilter.class.isAssignableFrom(event.getGeneratedBy()) ? AccountSession.Login.REMEMBER_ME
				: AccountSession.Login.INTERACTIVE;
		login(mode, event.getAuthentication());
	}

	/**
	 *
	 */
	@EventListener
	public void sessionFixationProtectionEvent(SessionFixationProtectionEvent event) {
		changeSessionId(event.getOldSessionId(), event.getNewSessionId());
	}

	/**
	 * Окончание сессии работы по таймауту HTTP-сессии
	 */
	@EventListener
	public void sessionDestroyed(HttpSessionDestroyedEvent event) {
		Authentication authentication = event.getSecurityContexts().stream()
				.map(SecurityContext::getAuthentication)
				.filter(Objects::nonNull)
				.findAny()
				.orElse(null);
		logout(authentication, event.getSession());
	}

	@Transactional
	private void login(AccountSession.Login mode, Authentication authentication) {
		WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
		Account account = accountRepo.getByUsernameAndIsActiveTrue(AccountService.toUserDetails(authentication).getUsername());//todo В этот момент пользователь уже может быть отключён
		AccountSession session = new AccountSession(account, mode, details.getSessionId());
		AccountSession save = sessionRepo.save(session);
		LOG.info("login(mode: {}, user: {}, sessionId: {}) => {}", mode, authentication.getName(), details.getSessionId(), save.getId());
	}

	@Transactional
	private void changeSessionId(String oldSessionId, String newSessionId) {
		int count = sessionRepo.changeSessionId(oldSessionId, newSessionId);
		LOG.info("changeSessionId({}, {}) => [{}]", oldSessionId, newSessionId, count);
	}

	@Transactional
	private void logout(Authentication authentication, HttpSession session) {
		long current = System.currentTimeMillis();
		long accessedTime = session.getLastAccessedTime();
		boolean isTimeOut = (current - accessedTime) > (session.getMaxInactiveInterval() * 1000L);
		AccountSession.Logout mode = isTimeOut ? AccountSession.Logout.TIMEOUT : AccountSession.Logout.MANUAL;
		int count = sessionRepo.logout(session.getId(), ZonedDateTime.now(), mode);
		LOG.info("logout(mode: {}, user: {}, sessionId: {}) => [{}]", mode, authentication != null ? authentication.getName() : null, session.getId(), count);
	}
}
