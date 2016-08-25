package name.valery1707.megatel.sorm.api.bro.files;

import name.valery1707.megatel.sorm.domain.BroFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BroFilesRepo extends JpaRepository<BroFiles, BigDecimal>, JpaSpecificationExecutor<BroFiles> {
	BroFiles getByExtracted(String extracted);

	List<BroFiles> findByFuid(String fuid);

	@Query("SELECT T FROM BroFiles AS T WHERE CONCAT(',', T.connUids, ',') LIKE CONCAT('%,', :uid, ',%')")
	List<BroFiles> findByConnUid(@Param("uid") String uid);
}
