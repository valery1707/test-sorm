package name.valery1707.megatel.sorm.api.bro.files;

import name.valery1707.megatel.sorm.domain.BroFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;

public interface BroFilesRepo extends JpaRepository<BroFiles, BigDecimal>, JpaSpecificationExecutor<BroFiles> {
	BroFiles getByExtracted(String extracted);
}
