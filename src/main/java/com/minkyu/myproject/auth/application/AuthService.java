package com.minkyu.myproject.auth.application;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.google.common.base.Preconditions;
import com.minkyu.myproject.auth.domain.AccessToken;
import com.minkyu.myproject.auth.domain.RefreshToken;
import com.minkyu.myproject.auth.domain.Token;
import com.minkyu.myproject.auth.domain.repository.RefreshTokenRepository;
import com.minkyu.myproject.auth.presentation.dto.JoinRequest;
import com.minkyu.myproject.auth.presentation.dto.LoginRequest;
import com.minkyu.myproject.common.model.Id;
import com.minkyu.myproject.security.jwt.Jwt;
import com.minkyu.myproject.user.domain.Email;
import com.minkyu.myproject.user.domain.Role;
import com.minkyu.myproject.user.domain.User;
import com.minkyu.myproject.user.domain.repository.UserRepository;
import jakarta.persistence.Access;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.net.URI;
import java.time.Instant;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;

    private final Jwt jwt;

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(PasswordEncoder passwordEncoder, Jwt jwt, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public URI join(JoinRequest dto) {
        Preconditions.checkArgument(dto != null, "dto must be provided.");

        Email email = dto.getEmail();
        String password = dto.getPassword();
        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(email, encodedPassword);
        userRepository.save(user);

        return URI.create("/users/me");
    }

    @Transactional
    public User login(LoginRequest dto) {
        Preconditions.checkArgument(dto != null, "dto must be provided.");

        Email email = dto.getEmail();
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);

        String rawPassword = dto.getPassword();
        String encodedPassword = user.getPassword();
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new IllegalArgumentException();
        }

        return user;
    }

    public Token<AccessToken> accessToken(Id<User, Long> id, Role role) {
        Jwt.Claims claims = Jwt.Claims.of(id, role);
        String accessToken = jwt.accessToken(claims);
        return Token.of(AccessToken.class, accessToken);
    }

    @org.springframework.transaction.annotation.Transactional
    public Token<AccessToken> reissue(Token<RefreshToken> token) {
        RefreshToken refreshToken = refreshTokenRepository.findByValue(token.value())
                .orElseThrow(() -> new NotFoundException("Not found Refresh Token."));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);

            Instant expiredAt = refreshToken.getExpiredAt();
            throw new TokenExpiredException("Refresh Token has expired.", expiredAt);
        }

        User user = refreshToken.getUser();

        Id<User, Long> id = user.getId();
        Role role = user.getRole();
        return accessToken(id, role);
    }

    @org.springframework.transaction.annotation.Transactional
    public Token<RefreshToken> refreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseGet(() -> {
                    RefreshToken token = jwt.refreshToken(user);
                    return refreshTokenRepository.save(token);
                });
        return Token.of(RefreshToken.class, refreshToken.value());
    }
}
