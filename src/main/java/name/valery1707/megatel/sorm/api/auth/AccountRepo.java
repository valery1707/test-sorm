package name.valery1707.megatel.sorm.api.auth;

import name.valery1707.megatel.sorm.domain.Account;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountRepo extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
	Account getByUsernameAndIsActiveTrue(String username) throws IncorrectResultSizeDataAccessException;
}
