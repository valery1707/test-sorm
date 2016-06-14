package name.valery1707.megatel.sorm.app.ssh;

import net.schmizz.sshj.xfer.InMemorySourceFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class SshInMemoryInput extends InMemorySourceFile {
	private final String name;
	private final long length;
	private final Supplier<InputStream> streamSupplier;

	public SshInMemoryInput(String name, long length, Supplier<InputStream> streamSupplier) {
		this.name = name;
		this.length = length;
		this.streamSupplier = streamSupplier;
	}

	public SshInMemoryInput(String name, String source) {
		this(name, source.length(), () -> new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)));
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getLength() {
		return length;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return streamSupplier.get();
	}
}
