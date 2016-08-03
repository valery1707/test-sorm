package name.valery1707.megatel.sorm.app.bro;

import net.schmizz.sshj.Config;
import net.schmizz.sshj.DefaultConfig;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

@Component
@Singleton
public class SshConfigurer {
	private Config config;

	@PostConstruct
	public void init() {
		config = new DefaultConfig();
	}

	public Config getConfig() {
		return config;
	}
}
