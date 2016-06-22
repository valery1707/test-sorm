package name.valery1707.megatel.sorm.api.task;

import name.valery1707.megatel.sorm.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepo extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
	/**
	 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/date-and-time-functions.html#function_sysdate">MySQL documentation for SYSDATE</a>
	 */
	@Query("SELECT T FROM Task AS T WHERE (SYSDATE() between T.periodStart AND T.periodFinish) AND (T.isActive IS TRUE)")
	List<Task> findActive();
}
