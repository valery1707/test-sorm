package name.valery1707.megatel.sorm.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import javax.inject.Inject;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//todo @Component
		UserDetailsService userDetailsService = new UserDetailsService() {
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				if (!username.equals("admin")) {
					throw new UsernameNotFoundException(String.format("Username '%s' not found", username));
				}
				return new User(username, "$2a$10$L0nYZKA.mJy0ky6V2dmBKudnGK4d4p6ggpbQcq3XZTshV7R2xywdi", true, true, true, true, Collections.emptyList());
			}
		};

		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
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
					.loginProcessingUrl("/login")
					.permitAll()
				.and()
				.httpBasic()
//				.and()//todo Implement RememberMe
//				.rememberMe()
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
