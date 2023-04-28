package com.minkyu.myproject.auth.domain.repository;

import com.minkyu.myproject.auth.domain.RefreshToken;
import com.minkyu.myproject.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUser(User user);

    Optional<RefreshToken> findByValue(String value);
}
