package name.valery1707.megatel.sorm;

import name.valery1707.megatel.sorm.domain.Data;
import name.valery1707.megatel.sorm.dto.DataDto;
import org.junit.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class ReflectionUtilsTest {
	@Test
	public void testConvert_StringInteger() throws Exception {
		Function<String, Integer> str2int = ReflectionUtils.findConverter(String.class, Integer.class);
		assertThat(str2int.apply("10")).isEqualTo(10);
	}

	@Test(expected = IllegalStateException.class)
	public void testConvert_StringInteger_bad() throws Exception {
		Function<String, Integer> str2int = ReflectionUtils.findConverter(String.class, Integer.class);
		assertThat(str2int.apply("xx")).isNotEqualTo(10);
	}

	@Test(expected = IllegalStateException.class)
	public void testConvert_Data() throws Exception {
		Function<Data, DataDto> data = ReflectionUtils.findConverter(Data.class, DataDto.class);
		assertThat(data.apply(new Data())).isNotNull();
	}

	@Test(expected = IllegalStateException.class)
	public void testConvert_Data_bad() throws Exception {
		Function<Data, DataDto> data = ReflectionUtils.findConverter(Data.class, DataDto.class);
		assertThat(data.apply(null)).isNotNull();
	}

	@Test(expected = IllegalStateException.class)
	public void testConvert_StringMath() throws Exception {
		ReflectionUtils.findConverter(String.class, Math.class);
	}
}