package name.valery1707.megatel.sorm.app.ssh;

import javaslang.collection.HashMap;
import javaslang.collection.List;
import name.valery1707.megatel.sorm.domain.Server;
import net.schmizz.sshj.Config;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.RemoteResourceFilter;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

public class SshClientHelper {
	private static final Logger LOG = LoggerFactory.getLogger(SshClientHelper.class);

	public static final String TMP_SUFFIX = ".tmp";

	private final Server server;
	private final Config config;
	private final Pattern isShellPrompt;
	private final Pattern isSudoPrompt;
	private SSHClient ssh;

	public SshClientHelper(Server server, Config config) {
		this.server = server;
		this.config = config;
		this.isShellPrompt = Pattern.compile("^(" + server.getUsername() + "@.+\\$|root@.+#)\\s+$");
		this.isSudoPrompt = Pattern.compile("^\\[sudo\\] password for " + server.getUsername() + ": $");
	}

	public void connect() throws IOException {
		ssh = new SSHClient(config);
		ssh.addHostKeyVerifier((hostname, port, key) -> true);//todo Доверяем любому ключу - это не секурно
		ssh.connect(server.getHost(), server.getPort());
		ssh.authPassword(server.getUsername(), server.getPassword());
		ssh.useCompression();
	}

	public void disconnect() {
		IOUtils.closeQuietly(ssh);
	}

	private String downloadFile(SFTPClient ftp, String path, Charset charset) throws IOException {
		SshInMemoryOutput output = new SshInMemoryOutput();
		ftp.get(path, output);
		return new String(output.getBytes(), charset);
	}

	private String downloadFile(SFTPClient ftp, String path) throws IOException {
		return downloadFile(ftp, path, StandardCharsets.UTF_8);
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
			String output = downloadFile(ftp, path);
			return value.equals(output);
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
		try (SFTPClient ftp = ssh.newSFTPClient()) {
			upload(ftp, files);
		}
	}

	private void upload(SFTPClient ftp, Map<File, String> files) throws IOException {
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

	private void write(Session.Shell shell, String command) throws IOException {
		OutputStream stream = shell.getOutputStream();

		stream.write(command.getBytes());//todo Charset?
		stream.flush();

		stream.write("\n".getBytes());
		stream.flush();
	}

	private List<String> read(Session.Shell shell) throws IOException {
		List<String> strings = List.empty();
		InputStream in = shell.getInputStream();
		StrBuilder last = new StrBuilder();
		int b;
		boolean stop = false;
		while (!stop && (b = in.read()) >= 0) {
			char c = (char) b;
			if (c == '\r' || c == '\n') {
				//Перевод каретки
				if (!last.isEmpty()) {
					//Если есть данные - добавляем их в буффер
					strings = strings.append(last.toString());
					last.clear();
				}//Если данных нет, то просто выкидываем их
			} else {
				last.append(c);
				stop = isShellPrompt.matcher(last).matches() || isSudoPrompt.matcher(last).matches();
			}
		}
		return strings.append(last.toString()).filter(StringUtils::isNotBlank).filter(s -> !isShellPrompt.matcher(s).matches());
	}

	public List<String> execute(String command) throws IOException {
		try (Session session = ssh.startSession()) {
			session.allocateDefaultPTY();
			try (Session.Shell shell = session.startShell()) {
				read(shell);
				write(shell, command);
				List<String> result = read(shell).remove(command);
				if (!result.isEmpty() && result.last().startsWith("[sudo]")) {
					write(shell, server.getPassword());
					result = read(shell);
				}
				return result;
			}
		}
	}

	public void checkConfig(File bro) throws IOException {
		List<String> userGroups = execute("groups").flatMap(s -> Arrays.asList(s.split(" ")));
		List<String> broLogGroup = execute("LANG=C stat --format=%G " + path(new File(bro, "logs")));
		List<String> groups = broLogGroup.removeAll(userGroups);
		if (!groups.isEmpty()) {
			throw new IOException("User must belong to group " + groups.mkString(", "));
		}
	}

	public void includeIntoBro(File broConfFile, File amtConfFile) throws IOException {
		String broConfPath = path(broConfFile);
		String amtConfPath = "@load " + path(amtConfFile);
		try (SFTPClient ftp = ssh.newSFTPClient()) {
			String broConf = downloadFile(ftp, broConfPath);
			if (!broConf.contains(amtConfPath)) {
				broConf += "\n" + "# Load AMT script\n" + amtConfPath + "\n";
				String broConfTmp = "/tmp/" + broConfFile.getName();
				upload(ftp, HashMap.of(new File(broConfTmp), broConf).toJavaMap());
				execute(String.format("sudo mv %s %s", broConfTmp, broConfPath));
				execute(String.format("sudo chown root:bro %s", broConfPath));
			}
		}
	}
}
