package name.valery1707.megatel.sorm;

import org.apache.commons.net.util.SubnetUtils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public final class IpUtils {
	public static InetAddress numberToIp(long src) {
		return numberToIp(BigInteger.valueOf(src));
	}

	public static InetAddress numberToIp(BigInteger src) {
		byte[] address = new byte[4];
		int pos = address.length - 1;

		while (src.compareTo(BigInteger.ZERO) > 0 && pos >= 0) {
			BigInteger remainder = src.remainder(BigInteger.valueOf(256));
			address[pos] = remainder.byteValue();
			pos--;
			src = src.divide(BigInteger.valueOf(256));
		}
		try {
			return InetAddress.getByAddress(address);
		} catch (UnknownHostException e) {
			return null;
		}
	}

	public static BigInteger ipToNumber(String src) {
		try {
			return ipToNumber(InetAddress.getByName(src));
		} catch (UnknownHostException e) {
			return null;
		}
	}

	public static BigInteger ipToNumber(InetAddress src) {
		BigInteger integer = BigInteger.valueOf(0);
		for (byte b : src.getAddress()) {
			long l = b < 0 ? 256 + b : b;
			integer = integer.multiply(BigInteger.valueOf(256));
			integer = integer.add(BigInteger.valueOf(l));
		}
		return integer;
	}

	public static SubnetUtils buildSubnet(String src) {
		try {
			SubnetUtils utils;
			if (src.contains("/")) {
				utils = new SubnetUtils(src);
			} else {
				utils = new SubnetUtils(src + "/32");
			}
			utils.setInclusiveHostCount(true);
			return utils;
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
}
