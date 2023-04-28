package com.minkyu.myproject.auth.presentation.dto;

import com.google.common.base.Preconditions;
import com.minkyu.myproject.auth.domain.AccessToken;
import com.minkyu.myproject.auth.domain.RefreshToken;
import com.minkyu.myproject.auth.domain.Token;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AuthTokens {
    private final String accessToken;

    private final String refreshToken;

    public AuthTokens(Token<AccessToken> accessToken, Token<RefreshToken> refreshToken) {
        Preconditions.checkArgument(accessToken != null, "access token must be provided.");
        Preconditions.checkArgument(refreshToken != null, "refresh token must be provided.");

        this.accessToken = accessToken.value();
        this.refreshToken = refreshToken.value();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("accessToken", accessToken)
                .append("refreshToken", refreshToken)
                .toString();
    }
}
