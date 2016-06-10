package name.valery1707.megatel.sorm.api.task.permit;

import name.valery1707.megatel.sorm.domain.TaskPermit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskPermitRepo extends JpaRepository<TaskPermit, Long>, JpaSpecificationExecutor<TaskPermit> {
}
