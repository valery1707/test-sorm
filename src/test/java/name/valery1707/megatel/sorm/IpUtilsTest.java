package name.valery1707.megatel.sorm;

import org.junit.Test;

import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

public class IpUtilsTest {
	@Test
	public void testIpToNumber() throws UnknownHostException {
		assertEquals("204.197.215.198", Long.toString(0xccc5d7c6L), IpUtils.ipToNumber("204.197.215.198").toString());
		assertEquals("50.18.215.115", Long.toString(0x3212d773L), IpUtils.ipToNumber("50.18.215.115").toString());
	}

	@Test
	public void testNumberToIp() throws UnknownHostException {
		assertEquals("204.197.215.198", "204.197.215.198", IpUtils.numberToIp(0xccc5d7c6L).getHostAddress());
		assertEquals("50.18.215.115", "50.18.215.115", IpUtils.numberToIp(0x3212d773L).getHostAddress());
	}
}
