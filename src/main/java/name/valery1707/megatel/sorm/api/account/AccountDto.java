package name.valery1707.megatel.sorm.api.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import name.valery1707.megatel.sorm.domain.Account;

import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDto {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	private long id;

	public AccountDto() {
	}

	public AccountDto(Account src) {
		this();
		setId(src.getId());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
