package name.valery1707.megatel.sorm.app.bro;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import name.valery1707.megatel.sorm.domain.Server;
import org.springframework.stereotype.Service;

import javax.inject.Singleton;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Singleton
public class BroLocker {

	private final LoadingCache<Long, ReentrantLock> locks = Caffeine.newBuilder()
			.build(id -> new ReentrantLock());

	public synchronized ReentrantLock lock(Server server) {
		return locks.get(server.getId());
	}
}
