package name.valery1707.megatel.sorm.db;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SpecificationModeTest {
	@Test
	public void testValues() throws Exception {
		for (SpecificationMode mode : SpecificationMode.values()) {
			assertEquals(mode, SpecificationMode.valueOf(mode.name()));
		}
	}
}