package name.valery1707.megatel.sorm.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import javax.inject.Inject;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Inject
	private UserDetailsService userDetailsService;

	public UserDetailsService userDetailsService() {
		return userDetailsService;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService());
		provider.setPasswordEncoder(new BCryptPasswordEncoder());
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
					.loginPage("/#/login")
					.permitAll()
			.and()
				.logout()
					.logoutUrl("/api/auth/logout")
			.and()
				.httpBasic()
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

	@Value("${security.csrf.name.fromClient}")
	private String csrfNameFromClient;

	@Value("${security.csrf.name.toClient}")
	private String csrfNameToClient;

	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName(csrfNameFromClient);
		return repository;
	}
}
