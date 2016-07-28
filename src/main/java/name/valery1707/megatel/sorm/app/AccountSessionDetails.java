package name.valery1707.megatel.sorm.app;

import javaslang.Tuple;
import javaslang.collection.List;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@SuppressWarnings("unused")
public class AccountSessionDetails extends WebAuthenticationDetails {
	private final String scheme;
	private final String protocol;
	private final String userAgent;
	private final String origin;
	private final String xRequestedWith;
	private final String acceptLanguage;

	public AccountSessionDetails(HttpServletRequest request) {
		super(request);
		List.ofAll(Collections.list(request.getHeaderNames())).toMap(name -> Tuple.of(name, request.getHeader(name))).stdout();
		scheme = request.getScheme();
		protocol = request.getProtocol();
		userAgent = request.getHeader("User-Agent");
		origin = request.getHeader("Origin");
		xRequestedWith = request.getHeader("X-Requested-With");
		acceptLanguage = request.getHeader("Accept-Language");
	}

	public String getScheme() {
		return scheme;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public String getOrigin() {
		return origin;
	}

	public String getxRequestedWith() {
		return xRequestedWith;
	}

	public String getAcceptLanguage() {
		return acceptLanguage;
	}
}
