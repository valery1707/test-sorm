package name.valery1707.core.utils;

import org.apache.commons.net.util.SubnetUtils;
import org.junit.Test;

import java.math.BigInteger;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IpUtilsTest {
	@Test
	public void testIpToNumber() throws UnknownHostException {
		assertEquals("204.197.215.198", Long.toString(0xccc5d7c6L), IpUtils.ipToNumber("204.197.215.198").toString());
		assertEquals("50.18.215.115", Long.toString(0x3212d773L), IpUtils.ipToNumber("50.18.215.115").toString());
		assertEquals("178.89.247.180", Long.toString(0xB259F7B4L), IpUtils.ipToNumber("178.89.247.180").toString());
		assertEquals("fe80::20c:29ff:feaa:296f", new BigInteger("338288524927261089654166435908610042223", 10).toString(), IpUtils.ipToNumber("fe80::20c:29ff:feaa:296f").toString());
	}

	@Test
	public void testNumberToIp() throws UnknownHostException {
		assertEquals("204.197.215.198", "204.197.215.198", IpUtils.numberToIp(0xccc5d7c6L).getHostAddress());
		assertEquals("50.18.215.115", "50.18.215.115", IpUtils.numberToIp(0x3212d773L).getHostAddress());
	}

	@Test
	public void testSubnet() throws Exception {
		SubnetUtils subnet;

		subnet = IpUtils.buildSubnet("127.0.0.1");
		assertNotNull("127.0.0.1#valid", subnet);
		assertEquals("127.0.0.1#count", 1L, subnet.getInfo().getAddressCountLong());
		assertEquals("127.0.0.1#low", "127.0.0.1", subnet.getInfo().getLowAddress());
		assertEquals("127.0.0.1#high", "127.0.0.1", subnet.getInfo().getHighAddress());

		subnet = IpUtils.buildSubnet("192.168.10.17/24");
		assertNotNull("192.168.10.17/24#valid", subnet);
		assertEquals("192.168.10.17/24#count", 256L, subnet.getInfo().getAddressCountLong());
		assertEquals("192.168.10.17/24#low", "192.168.10.0", subnet.getInfo().getLowAddress());
		assertEquals("192.168.10.17/24#high", "192.168.10.255", subnet.getInfo().getHighAddress());
	}
}
