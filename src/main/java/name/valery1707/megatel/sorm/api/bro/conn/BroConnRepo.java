package name.valery1707.megatel.sorm.api.bro.conn;

import name.valery1707.megatel.sorm.domain.BroConn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;

public interface BroConnRepo extends JpaRepository<BroConn, BigDecimal>, JpaSpecificationExecutor<BroConn> {
}
