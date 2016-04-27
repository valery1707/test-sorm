package name.valery1707.megatel.sorm.api.smtp.http;

import name.valery1707.megatel.sorm.domain.Smtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;

public interface SmtpRepo extends JpaRepository<Smtp, BigDecimal>, JpaSpecificationExecutor<Smtp> {
}
