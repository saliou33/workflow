package com.innov.workflow.core.domain.repository.auth;

import com.innov.workflow.core.domain.entity.User;
import com.innov.workflow.core.domain.entity.auth.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    RefreshToken findByUser(User user);

    void deleteByToken(String token);

    void deleteByUser(User user);
}
