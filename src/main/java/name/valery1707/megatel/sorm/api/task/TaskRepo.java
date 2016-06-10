package name.valery1707.megatel.sorm.api.task;

import name.valery1707.megatel.sorm.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepo extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
	@Query("SELECT T FROM Task AS T WHERE (current_timestamp between T.periodStart AND T.periodFinish)")
	List<Task> findActive();
}
