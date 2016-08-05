package name.valery1707.core.configuration;

import name.valery1707.core.app.AccountSessionDetailsSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Inject
	private UserDetailsService userDetailsService;

	public UserDetailsService userDetailsService() {
		return userDetailsService;
	}

	@Bean
	@Singleton
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService());
		provider.setPasswordEncoder(passwordEncoder());
		auth.authenticationProvider(provider);
	}

	@Inject
	private SecurityProperties securityProperties;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http
				.authorizeRequests()
					.antMatchers("/api/auth").permitAll()
					.antMatchers(securityProperties.getBasic().getPath()).authenticated()
			.and()
				.formLogin()
					.loginPage("/#/login").permitAll()
					.authenticationDetailsSource(authenticationDetailsSource())
			.and()
				.logout()
					.logoutUrl("/api/auth/logout")
			.and()
				.httpBasic()
					.authenticationDetailsSource(authenticationDetailsSource())
			.and()
				.rememberMe()
					.rememberMeParameter("rememberMe")
//					.key()//Рандомный ключ сбрасывает старые сесси при перезапуске сервера
					.key("---remember-me---static---key---")//Постоянный ключ позволяет автологиниться после перезапуска сервера, если не менялся пароль
					.tokenValiditySeconds(AbstractRememberMeServices.TWO_WEEKS_S)
//					.tokenRepository(null)//Сами токены можно хранить где-нибудь, куда можно дотянуться самостоятельно с разными целями
			.and()
		;
		if (!securityProperties.isEnableCsrf()) {
			http.csrf().disable();
		} else {
			http
				.csrf().csrfTokenRepository(csrfTokenRepository())
				.and()
				.addFilterAfter(new CsrfCookieFilter(csrfNameToClient), CsrfFilter.class);
		}
		// @formatter:on
	}

	@Bean
	@Singleton
	public AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
		return new AccountSessionDetailsSource();
	}

	@Value("${security.csrf.name.fromClient}")
	private String csrfNameFromClient;

	@Value("${security.csrf.name.toClient}")
	private String csrfNameToClient;

	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName(csrfNameFromClient);
		return repository;
	}

	@Bean
	public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
		//Можно и напрямую возвращать класс как бин, но так вроде корректнее
		return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
	}
}
