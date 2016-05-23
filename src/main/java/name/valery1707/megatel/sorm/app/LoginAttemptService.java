package name.valery1707.megatel.sorm.app;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

@Service
@Singleton
public class LoginAttemptService {
	private static final Logger LOG = LoggerFactory.getLogger(LoginAttemptService.class);

	private LoadingCache<String, MutableInt> attempts = Caffeine.newBuilder()
			.maximumSize(10_000)
			.expireAfterAccess(60, TimeUnit.MINUTES)//После 60 минут мы полностью забываем об попытках доступа
			.refreshAfterWrite(10, TimeUnit.MINUTES)//Каждые 10 минут уменьшаем счётчик в 10 раз
			.build(new CacheLoaderImpl());

	public void success(Authentication authentication) {
		WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
		success(AccountUtils.toUserDetails(authentication).getUsername(), details.getRemoteAddress());
	}

	public void success(String username, String clientIP) {
		String key = toKey(username, clientIP);
		LOG.debug("success({}, {})", key, attempts.get(key).getValue());
		attempts.invalidate(key);
	}

	public void preventBruteForce(String username, String clientIP) {
		String key = toKey(username, clientIP);
		int attemptsCount = incAndGet(key);
		int waitCount = (int) Math.pow(2.0, attemptsCount / 10) - 1;
		LOG.debug("attempt(user: {}, attempt: {}, wait: {})", key, attemptsCount, waitCount);
		while (waitCount > 0 && !Thread.interrupted()) {
			try {
				LOG.trace("sleep(user: {}, count: {})", key, waitCount);
				Thread.sleep(1000L);
				waitCount--;
			} catch (InterruptedException e) {
				LOG.warn("Thread was interrupted", e);
				waitCount = 0;
			}
		}
	}

	private int incAndGet(String key) {
		MutableInt count = attempts.get(key);
		int value = count.getValue();
		count.increment();
		return value;
	}

	@NotNull
	private String toKey(String username, String clientIP) {
		return username + "@" + clientIP;
	}

	private static class CacheLoaderImpl implements CacheLoader<String, MutableInt> {

		@Override
		public MutableInt load(@Nonnull String key) throws Exception {
			LOG.trace("load({}): 0", key);
			return new MutableInt(0);
		}

		@Override
		public MutableInt reload(@Nonnull String key, @Nonnull MutableInt oldValue) throws Exception {
			MutableInt res = new MutableInt(oldValue.getValue() / 10);
			LOG.trace("reload({}, {}): {}", key, oldValue.getValue(), res);
			return res;
		}
	}
}
