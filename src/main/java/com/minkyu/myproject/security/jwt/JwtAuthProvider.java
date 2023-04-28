package com.minkyu.myproject.security.jwt;

import com.minkyu.myproject.auth.application.AuthService;
import com.minkyu.myproject.auth.domain.AccessToken;
import com.minkyu.myproject.auth.domain.RefreshToken;
import com.minkyu.myproject.auth.domain.Token;
import com.minkyu.myproject.auth.presentation.dto.AuthTokens;
import com.minkyu.myproject.auth.presentation.dto.LoginRequest;
import com.minkyu.myproject.common.exception.NotFoundException;
import com.minkyu.myproject.common.model.Id;
import com.minkyu.myproject.user.domain.Role;
import com.minkyu.myproject.user.domain.User;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtAuthProvider implements AuthenticationProvider {
    private final AuthService authService;

    public JwtAuthProvider(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthToken jwtAuthToken = (JwtAuthToken) authentication;
        return processUserAuth(jwtAuthToken.loginRequest());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ClassUtils.isAssignable(JwtAuthToken.class, authentication);
    }

    private Authentication processUserAuth(LoginRequest loginRequest) {
        try {
            User user = authService.login(loginRequest);

            Id<User, Long> id = user.getId();
            Role role = user.getRole();
            JwtAuth jwtAuth = new JwtAuth(id, role);

            String roleName = role.name();
            List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(roleName);

            JwtAuthToken jwtAuthToken = new JwtAuthToken(jwtAuth, null, authorityList);

            Token<AccessToken> accessToken = authService.accessToken(id, role);
            Token<RefreshToken> refreshToken = authService.refreshToken(user);
            jwtAuthToken.setDetails(new AuthTokens(accessToken, refreshToken));

            return jwtAuthToken;
        } catch (NotFoundException | IllegalArgumentException e) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 틀립니다.");
        }
    }
}
