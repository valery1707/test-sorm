package name.valery1707.megatel.sorm.api.auth;

import name.valery1707.megatel.sorm.domain.AccountSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

public interface AccountSessionRepo extends JpaRepository<AccountSession, Long>, JpaSpecificationExecutor<AccountSession> {
	@Modifying
	@Transactional
	@Query("UPDATE AccountSession s SET s.sessionId = :newSessionId WHERE (s.sessionId = :oldSessionId) AND (s.logoutAt IS NULL)")
	int changeSessionId(@Param("oldSessionId") String oldSessionId, @Param("newSessionId") String newSessionId);

	@Modifying
	@Transactional
	@Query("UPDATE AccountSession s SET s.logoutAt = :logoutAt, s.logoutAs = :logoutAs WHERE s.sessionId = :sessionId")
	int logout(@Param("sessionId") String sessionId, @Param("logoutAt") ZonedDateTime logoutAt, @Param("logoutAs") AccountSession.Logout logoutAs);
}
