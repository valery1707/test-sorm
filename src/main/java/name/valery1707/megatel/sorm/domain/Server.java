package name.valery1707.megatel.sorm.domain;

import name.valery1707.core.domain.ABaseEntity;
import name.valery1707.core.domain.Account;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@SuppressWarnings("unused")
public class Server extends ABaseEntity {
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

	@NotNull
	private String host;

	@Min(0)
	private int port;

	@NotNull
	private String username;

	@NotNull
	private String password;

	@NotNull
	private String broPath;

	@NotNull
	private String confPath;

	@NotNull
	private String dbName;

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

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBroPath() {
		return broPath;
	}

	public void setBroPath(String broPath) {
		this.broPath = broPath;
	}

	public String getConfPath() {
		return confPath;
	}

	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
}
