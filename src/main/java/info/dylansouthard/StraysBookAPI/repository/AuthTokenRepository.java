package info.dylansouthard.StraysBookAPI.repository;

import info.dylansouthard.StraysBookAPI.model.enums.AuthTokenType;
import info.dylansouthard.StraysBookAPI.model.user.AuthToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    @Query("DELETE FROM AuthToken t WHERE t.expiresAt < :now")
    @Modifying
    @Transactional
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM AuthToken t WHERE t.user.id = :userId AND t.type = :type")
    List<AuthToken> findTokensByType(@Param("userId") Long userId, @Param("type") AuthTokenType type);

}
