package name.valery1707.core.configuration;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CsrfCookieFilter extends OncePerRequestFilter {

	private final String csrfCookie;

	public CsrfCookieFilter(String csrfCookie) {
		this.csrfCookie = csrfCookie;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		if (csrf != null) {
			Cookie cookie = WebUtils.getCookie(request, csrfCookie);
			String token = csrf.getToken();
			if (cookie == null || token != null && !token.equals(cookie.getValue())) {
				cookie = new Cookie(csrfCookie, token);
				cookie.setPath(detectCookiePath(request));
				response.addCookie(cookie);
			}
		}
		filterChain.doFilter(request, response);
	}

	private String detectCookiePath(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, "JSESSIONID");
		if (cookie != null && cookie.getPath() != null) {
			return cookie.getPath();
		}
		String contextPath = request.getContextPath();
		return contextPath.length() > 0 ? contextPath : "/";
	}
}
