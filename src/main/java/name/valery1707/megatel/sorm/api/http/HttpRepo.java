package name.valery1707.megatel.sorm.api.http;

import name.valery1707.megatel.sorm.domain.Http;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface HttpRepo extends JpaRepository<Http, BigDecimal>, JpaSpecificationExecutor<Http> {
	@Query("SELECT H FROM Http AS H WHERE (H.uid = :uid) AND (CONCAT(',', H.origFuids, ',') LIKE CONCAT(',', :fuid, ',') OR CONCAT(',', H.respFuids, ',') LIKE CONCAT(',', :fuid, ','))")
	List<Http> findByUidAndFuid(@Param("uid") String uid, @Param("fuid") String fuid);
}
