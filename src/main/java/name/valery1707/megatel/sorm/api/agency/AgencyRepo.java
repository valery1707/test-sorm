package name.valery1707.megatel.sorm.api.agency;

import name.valery1707.megatel.sorm.domain.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AgencyRepo extends JpaRepository<Agency, Long>, JpaSpecificationExecutor<Agency> {
}
