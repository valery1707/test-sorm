package name.valery1707.megatel.sorm.app.ssh;

import name.valery1707.megatel.sorm.domain.Server;
import net.schmizz.sshj.Config;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.RemoteResourceFilter;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class SshClientHelper {
	private static final Logger LOG = LoggerFactory.getLogger(SshClientHelper.class);

	public static final String TMP_SUFFIX = ".tmp";

	private final Server server;
	private final Config config;
	private SSHClient ssh;

	public SshClientHelper(Server server, Config config) {
		this.server = server;
		this.config = config;
	}

	public void connect() throws IOException {
		ssh = new SSHClient(config);
		ssh.addHostKeyVerifier((hostname, port, key) -> true);//todo Доверяем любому ключу - это не секурно
		ssh.connect(server.getHost(), server.getPort());
		ssh.authPassword(server.getUsername(), server.getPassword());
		//ssh.useCompression();//todo Make sure JZlib is in classpath for this to work
	}

	public void disconnect() {
		IOUtils.closeQuietly(ssh);
	}

	public boolean hasSameContent(File file, String value) throws IOException {
		LOG.debug("CheckContent({})", file.getName());
		String path = path(file);
		SFTPClient ftp = null;
		try {
			ftp = ssh.newSFTPClient();
			FileAttributes attributes = ftp.statExistence(path);
			if (attributes == null) {
				return false;
			}
			if (attributes.getSize() != value.length()) {
				return false;
			}
			SshInMemoryOutput output = new SshInMemoryOutput();
			ftp.get(path, output);
			return value.equals(new String(output.getBytes(), StandardCharsets.UTF_8));
		} finally {
			IOUtils.closeQuietly(ftp);
		}
	}

	private String path(File file) {
		if (File.separatorChar == '\\') {
			return file.getPath().replace('\\', '/');
		} else {
			return file.getPath();
		}
	}

	public void cleanTmp(File dir) throws IOException {
		clean(dir, IS_TMP);
	}

	public void clean(File dir, RemoteResourceFilter filter) throws IOException {
		SFTPClient ftp = null;
		try {
			ftp = ssh.newSFTPClient();
			for (RemoteResourceInfo file : ftp.ls(path(dir), filter)) {
				LOG.debug("Remove({})", file.getName());
				ftp.rm(file.getPath());
			}
		} finally {
			IOUtils.closeQuietly(ftp);
		}
	}

	private static final RemoteResourceFilter IS_TMP = resource -> resource.isRegularFile() && resource.getName().endsWith(TMP_SUFFIX);

	public void upload(Map<File, String> files) throws IOException {
		SFTPClient ftp = null;
		try {
			ftp = ssh.newSFTPClient();
			//Загрузка всех файлов во временные
			for (Map.Entry<File, String> entry : files.entrySet()) {
				File path = entry.getKey();
				String value = entry.getValue();
				LOG.debug("Upload({}, {})", path.getName(), value.length());
				ftp.put(new SshInMemoryInput(path.getName(), value), path(path) + TMP_SUFFIX);
			}
			//Переименование
			for (File file : files.keySet()) {
				String pathNew = path(file);
				String pathOld = pathNew + TMP_SUFFIX;
				LOG.debug("Rename({})", file.getName());
				if (ftp.statExistence(pathNew) != null) {
					ftp.rm(pathNew);
				}
				ftp.rename(pathOld, pathNew);
			}
		} finally {
			IOUtils.closeQuietly(ftp);
		}
	}

	public void mkdir(File dir) throws IOException {
		String path = path(dir);
		SFTPClient ftp = null;
		try {
			ftp = ssh.newSFTPClient();
			FileAttributes attributes = ftp.statExistence(path);
			if (attributes == null) {
				LOG.debug("MkDirs({})", path);
				ftp.mkdirs(path);
			}
		} finally {
			IOUtils.closeQuietly(ftp);
		}
	}
}
