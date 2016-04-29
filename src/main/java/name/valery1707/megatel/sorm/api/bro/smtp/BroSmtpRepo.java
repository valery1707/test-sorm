package name.valery1707.megatel.sorm.api.bro.smtp;

import name.valery1707.megatel.sorm.domain.BroSmtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;

public interface BroSmtpRepo extends JpaRepository<BroSmtp, BigDecimal>, JpaSpecificationExecutor<BroSmtp> {
}
