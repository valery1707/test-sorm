package name.valery1707.megatel.sorm.api.http;

import name.valery1707.megatel.sorm.domain.Http;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.util.List;

public interface HttpRepo extends JpaRepository<Http, BigDecimal>, JpaSpecificationExecutor<Http> {
	List<Http> findByUid(String uid);
}
