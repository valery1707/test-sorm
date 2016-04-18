package name.valery1707.megatel.sorm.api.conn;

import name.valery1707.megatel.sorm.domain.Conn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;

public interface ConnRepo extends JpaRepository<Conn, BigDecimal>, JpaSpecificationExecutor<Conn> {
}
