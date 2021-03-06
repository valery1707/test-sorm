package name.valery1707.core.app;

import javaslang.collection.List;
import name.valery1707.core.domain.AccountSession;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionFixationProtectionEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

@Service
@Singleton
public class AccountSessionListener {

	@Inject
	private AccountSessionService sessionService;

	@Inject
	private LoginAttemptService loginAttemptService;

	@PostConstruct
	public void init() {
		sessionService.serverRestart();
	}

	@EventListener
	public void authenticationFailureEvent(AbstractAuthenticationFailureEvent event) {
		sessionService.loginFail(
				event.getAuthentication().getName(), event.getAuthentication().getCredentials().toString(),
				((AccountSessionDetails) event.getAuthentication().getDetails()).getRemoteAddress()
		);
	}

	/**
	 * Начало сессии работы с полной авторизацией
	 */
	@EventListener
	public void authenticationSuccessEvent(AuthenticationSuccessEvent event) {
		sessionService.login(AccountSession.Login.MANUAL, event.getAuthentication());
		loginAttemptService.success(event.getAuthentication());
	}

	/**
	 * Начало сессии работы с автоматической авторизацией (RememberMe)
	 */
	@EventListener
	public void interactiveAuthenticationSuccessEvent(InteractiveAuthenticationSuccessEvent event) {
		AccountSession.Login mode = RememberMeAuthenticationFilter.class.isAssignableFrom(event.getGeneratedBy()) ? AccountSession.Login.REMEMBER_ME
				: AccountSession.Login.INTERACTIVE;
		sessionService.login(mode, event.getAuthentication());
		loginAttemptService.success(event.getAuthentication());
	}

	/**
	 * Изменение ID сессии для защиты от атаки "фиксация сессии"
	 */
	@EventListener
	public void sessionFixationProtectionEvent(SessionFixationProtectionEvent event) {
		sessionService.changeSessionId(event.getOldSessionId(), event.getNewSessionId());
	}

	/**
	 * Окончание сессии работы по таймауту HTTP-сессии
	 */
	@EventListener
	public void sessionDestroyed(HttpSessionDestroyedEvent event) {
		Authentication authentication = List.ofAll(event.getSecurityContexts())
				.map(SecurityContext::getAuthentication)
				.find(Objects::nonNull)
				.toJavaOptional()
				.orElse(null);
		sessionService.logout(authentication, event.getSession());
	}
}
