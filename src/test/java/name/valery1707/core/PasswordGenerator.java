package name.valery1707.core;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		for (String arg : args) {
			System.out.println(String.format("'%s' => '%s'", arg, encoder.encode(arg)));
		}
	}
}
