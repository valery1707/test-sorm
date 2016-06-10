package name.valery1707.megatel.sorm.api.task.permit;

import name.valery1707.megatel.sorm.domain.Account;
import name.valery1707.megatel.sorm.domain.TaskPermit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface TaskPermitRepo extends JpaRepository<TaskPermit, Long>, JpaSpecificationExecutor<TaskPermit> {
	@Query("SELECT TP FROM TaskPermit AS TP WHERE (TP.account = :account) AND (:time BETWEEN TP.periodStart AND TP.periodFinish)")
	List<TaskPermit> findAllowedAtTime(@Param("account") Account account, @Param("time") ZonedDateTime time);
}
