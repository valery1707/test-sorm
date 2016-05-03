package name.valery1707.megatel.sorm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.nio.file.*;

import static org.junit.Assert.assertNotNull;

public class FilesystemNotifyTest {
	@Test
	@Ignore
	public void testNotify() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		File file = new File("build/tmp/notify.txt").getParentFile();
		WatchService watchService = FileSystems.getDefault().newWatchService();
		file.toPath().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
		WatchKey watchKey = watchService.take();
		System.out.println("watchKey = " + mapper.writeValueAsString(watchKey));
		for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
			System.out.println("watchEvent = " + watchEvent);
			System.out.println("watchEvent.kind() = " + watchEvent.kind());
			System.out.println("watchEvent.count() = " + watchEvent.count());
			System.out.println("watchEvent.context() = " + watchEvent.context());
			System.out.println("watchEvent.context().getClass() = " + watchEvent.context().getClass());
			System.out.println("watchEvent.context() instanceof Path = " + (watchEvent.context() instanceof Path));
		}
		assertNotNull(watchKey);
	}
}
