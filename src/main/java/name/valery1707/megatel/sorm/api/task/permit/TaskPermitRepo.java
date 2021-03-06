package name.valery1707.megatel.sorm.api.task.permit;

import name.valery1707.core.domain.Account;
import name.valery1707.megatel.sorm.domain.TaskPermit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public interface TaskPermitRepo extends JpaRepository<TaskPermit, Long>, JpaSpecificationExecutor<TaskPermit> {
	@Query("SELECT TP FROM TaskPermit AS TP WHERE (TP.account = :account) AND (:time BETWEEN TP.periodStart AND TP.periodFinish)")
	List<TaskPermit> findAllowedAtTime(@Param("account") Account account, @Param("time") ZonedDateTime time);

	@Query("SELECT TP.task.id FROM TaskPermit AS TP WHERE (TP.account = :account) AND (:time BETWEEN TP.periodStart AND TP.periodFinish)")
	Set<Long> findAllowedTaskAtTime(@Param("account") Account account, @Param("time") ZonedDateTime time);

	@Query("SELECT COUNT(TP.task.id) > 0 FROM TaskPermit AS TP WHERE (TP.account = :account) AND (:time BETWEEN TP.periodStart AND TP.periodFinish) AND (TP.task.id = :taskId)")
	boolean isAllowedTask(@Param("account") Account account, @Param("time") ZonedDateTime time, @Param("taskId") Long taskId);
}
