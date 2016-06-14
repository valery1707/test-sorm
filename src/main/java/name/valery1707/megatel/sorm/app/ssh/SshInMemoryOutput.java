package name.valery1707.megatel.sorm.app.ssh;

import net.schmizz.sshj.xfer.InMemoryDestFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SshInMemoryOutput extends InMemoryDestFile {
	private final ByteArrayOutputStream stream;

	public SshInMemoryOutput() {
		stream = new ByteArrayOutputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return stream;
	}


	public byte[] getBytes() {
		return stream.toByteArray();
	}
}
