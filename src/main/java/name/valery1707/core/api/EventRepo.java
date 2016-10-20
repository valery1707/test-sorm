package name.valery1707.core.api;

import name.valery1707.core.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventRepo extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
}
