package name.valery1707.megatel.sorm.api.bro.http;

import name.valery1707.megatel.sorm.domain.BroHttp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BroHttpRepo extends JpaRepository<BroHttp, BigDecimal>, JpaSpecificationExecutor<BroHttp> {
	@Query("SELECT H FROM BroHttp AS H WHERE (H.uid = :uid) AND (CONCAT(',', H.origFuids, ',') LIKE CONCAT(',', :fuid, ',') OR CONCAT(',', H.respFuids, ',') LIKE CONCAT(',', :fuid, ','))")
	List<BroHttp> findByUidAndFuid(@Param("uid") String uid, @Param("fuid") String fuid);
}
