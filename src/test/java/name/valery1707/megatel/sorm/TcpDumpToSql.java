package name.valery1707.megatel.sorm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static name.valery1707.megatel.sorm.IpUtils.ipToNumber;

public class TcpDumpToSql {
	private static final Pattern CSV = Pattern.compile("\",\"");
	private static final List<String> HEADERS = Arrays.asList("\"No.", "DateTime", "src_ip", "src_port", "dst_ip", "dst_port", "Protocol");
	private static final Map<String, Integer> links = new HashMap<String, Integer>();

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(args[0]));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] split = CSV.split(line);
			if (links.isEmpty()) {
				//Fill header links
				int index = 0;
				for (String s : split) {
					if (HEADERS.contains(s)) {
						links.put(s, index);
					}
					index++;
				}
			} else {
				//Process body
				boolean isFirst = true;
				System.out.print("\t(");
				for (String h : HEADERS) {
					String s = split[links.get(h)];
					if (h.startsWith("\"")) {
						s = s.substring(1);
					} else if (h.endsWith("_ip")) {
						s = ipToNumber(s).toString();
					} else if (h.equals("Protocol") || h.equals("DateTime")) {
						s = "'" + s + "'";
					}
					if (!isFirst) {
						System.out.print(", ");
					}
					System.out.print(s);
					isFirst = false;
				}
				System.out.println("),");
			}
		}
	}
}
