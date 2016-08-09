package name.valery1707.megatel.sorm.api.agency;

import com.fasterxml.jackson.annotation.JsonInclude;
import name.valery1707.core.api.BaseDto;
import name.valery1707.megatel.sorm.domain.Agency;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgencyDto implements BaseDto {
	private long id;

	@NotNull
	@Size(min = 3)
	private String name;

	public AgencyDto() {
	}

	public AgencyDto(Agency src) {
		this();
		setId(src.getId());
		setName(src.getName());
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
