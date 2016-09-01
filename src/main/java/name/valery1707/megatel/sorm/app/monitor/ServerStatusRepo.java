package name.valery1707.megatel.sorm.app.monitor;

import name.valery1707.megatel.sorm.domain.ServerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServerStatusRepo extends JpaRepository<ServerStatus, Long>, JpaSpecificationExecutor<ServerStatus> {
}
