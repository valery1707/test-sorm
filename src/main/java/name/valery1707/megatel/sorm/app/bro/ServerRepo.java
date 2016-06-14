package name.valery1707.megatel.sorm.app.bro;

import name.valery1707.megatel.sorm.domain.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServerRepo extends JpaRepository<Server, Long>, JpaSpecificationExecutor<Server> {
}
