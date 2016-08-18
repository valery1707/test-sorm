package name.valery1707.megatel.sorm.api.bro.binary;

import name.valery1707.megatel.sorm.domain.BroBinary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;

public interface BroBinaryRepo extends JpaRepository<BroBinary, BigDecimal>, JpaSpecificationExecutor<BroBinary> {
}
