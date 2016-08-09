package name.valery1707.megatel.sorm.domain;

import name.valery1707.core.domain.ABaseEntity;
import name.valery1707.core.domain.Account;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Наименование органа, осуществляющего проведение ОРМ или надзор
 */
@Entity
@SuppressWarnings("unused")
public class Agency extends ABaseEntity {
	@NotNull
	@ManyToOne
	@CreatedBy
	private Account createdBy;

	@NotNull
	@CreatedDate
	private ZonedDateTime createdAt;

	@NotNull
	@ManyToOne
	@LastModifiedBy
	private Account modifiedBy;

	@NotNull
	@LastModifiedDate
	private ZonedDateTime modifiedAt;

	@NotNull
	private String name;

	public Account getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Account createdBy) {
		this.createdBy = createdBy;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Account getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Account modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public ZonedDateTime getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(ZonedDateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public static final class AgencyBuilder {
		private Account createdBy;
		private ZonedDateTime createdAt;
		private Account modifiedBy;
		private ZonedDateTime modifiedAt;
		private String name;
		private Long id;

		private AgencyBuilder() {
		}

		public static AgencyBuilder anAgency() {
			return new AgencyBuilder();
		}

		public AgencyBuilder withCreatedBy(Account createdBy) {
			this.createdBy = createdBy;
			return this;
		}

		public AgencyBuilder withCreatedAt(ZonedDateTime createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		public AgencyBuilder withModifiedBy(Account modifiedBy) {
			this.modifiedBy = modifiedBy;
			return this;
		}

		public AgencyBuilder withModifiedAt(ZonedDateTime modifiedAt) {
			this.modifiedAt = modifiedAt;
			return this;
		}

		public AgencyBuilder withName(String name) {
			this.name = name;
			return this;
		}

		public AgencyBuilder withId(Long id) {
			this.id = id;
			return this;
		}

		public Agency build() {
			Agency agency = new Agency();
			agency.setCreatedBy(createdBy);
			agency.setCreatedAt(createdAt);
			agency.setModifiedBy(modifiedBy);
			agency.setModifiedAt(modifiedAt);
			agency.setName(name);
			agency.setId(id);
			return agency;
		}
	}
}
