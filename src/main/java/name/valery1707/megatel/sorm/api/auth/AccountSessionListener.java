package name.valery1707.megatel.sorm.api.auth;

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

import javax.inject.Singleton;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@Service
@Singleton
public class AccountSessionListener {
	private static final Logger LOG = LoggerFactory.getLogger(AccountSessionListener.class);

	/**
	 * Начало сессии работы с полной авторизацией
	 */
	@EventListener
	public void authenticationSuccessEvent(AuthenticationSuccessEvent event) {
		login("REAL", event.getAuthentication());
	}

	/**
	 * Начало сессии работы с автоматической авторизацией (RememberMe)
	 */
	@EventListener
	public void interactiveAuthenticationSuccessEvent(InteractiveAuthenticationSuccessEvent event) {
		String mode = RememberMeAuthenticationFilter.class.isAssignableFrom(event.getGeneratedBy()) ? "REMEMBER_ME"
				: "INTERACTIVE";
		login(mode, event.getAuthentication());
	}

	/**
	 *
	 */
	@EventListener
	public void sessionFixationProtectionEvent(SessionFixationProtectionEvent event) {
		LOG.info("changeSessionId({}, {})", event.getOldSessionId(), event.getNewSessionId());
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

	private void login(String mode, Authentication authentication) {
		WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
		LOG.info("login(mode: {}, user: {}, sessionId: {})", mode, authentication.getName(), details.getSessionId());
		//todo Сохранение в БД новой сесии
	}

	private void logout(Authentication authentication, HttpSession session) {
		long current = System.currentTimeMillis();
		long accessedTime = session.getLastAccessedTime();
		boolean isTimeOut = (current - accessedTime) > (session.getMaxInactiveInterval() * 1000L);
		String mode = isTimeOut ? "TIMEOUT" : "MANUAL";
		LOG.info("logout(mode: {}, user: {}, sessionId: {})", mode, authentication != null ? authentication.getName() : null, session.getId());
		//todo Закрыть в БД активную сессию
	}
}
