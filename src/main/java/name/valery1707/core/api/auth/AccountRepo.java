package name.valery1707.core.api.auth;

import name.valery1707.core.domain.Account;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepo extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
	//У нас может быть несолько Неактивных аккаунтов с одинаковым именем, но только один активный
	Account getByUsernameAndIsActiveTrue(String username) throws IncorrectResultSizeDataAccessException;

	@Modifying
	@Query("UPDATE Account AS a SET a.password = :password WHERE a.id = :id")
	int setPasswordById(@Param("password") String password, @Param("id") long id);
}
