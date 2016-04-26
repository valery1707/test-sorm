package name.valery1707.megatel.sorm.api.files;

import name.valery1707.megatel.sorm.domain.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;

public interface FilesRepo extends JpaRepository<Files, BigDecimal>, JpaSpecificationExecutor<Files> {
	Files getByExtracted(String extracted);
}
