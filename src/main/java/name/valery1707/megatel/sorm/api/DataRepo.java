package name.valery1707.megatel.sorm.api;

import name.valery1707.megatel.sorm.domain.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataRepo extends JpaRepository<Data, Long>, JpaSpecificationExecutor<Data> {
}
