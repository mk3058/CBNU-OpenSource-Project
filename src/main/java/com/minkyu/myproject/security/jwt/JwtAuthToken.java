package com.minkyu.myproject.security.jwt;

import com.google.common.base.Preconditions;
import com.minkyu.myproject.auth.presentation.dto.LoginRequest;
import com.minkyu.myproject.user.domain.Email;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthToken extends AbstractAuthenticationToken {

    private final Object principal;

    private String credentials;

    public JwtAuthToken(LoginRequest dto) {
        super(null);
        super.setAuthenticated(false);

        Preconditions.checkArgument(dto!=null, "dto must be provided.");

        this.principal=dto.getEmail();
        this.credentials=dto.getPassword();
    }

    public JwtAuthToken(Object principal, String credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);

        Preconditions.checkArgument(principal!=null, "principal must be provided.");

        this.principal = principal;
        this.credentials = credentials;
    }

    LoginRequest loginRequest() {
        Email email = (Email)principal;
        String password = credentials;
        return new LoginRequest(email, password);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();;
        credentials=null;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(ToStringStyle.JSON_STYLE)
                .append("principal", principal)
                .append("credentials", credentials)
                .toString();
    }
}
