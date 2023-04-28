package com.minkyu.myproject.security.jwt;

import com.google.common.base.Preconditions;
import com.minkyu.myproject.common.model.Id;
import com.minkyu.myproject.user.domain.Role;
import com.minkyu.myproject.user.domain.User;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class JwtAuth {

    private final Id<User, Long> id;

    private final Role role;

    public JwtAuth(Id<User, Long> id, Role role) {
        Preconditions.checkArgument(id!=null, "id must be provided.");
        Preconditions.checkArgument(role!=null, "role must be provided.");

        this.id = id;
        this.role = role;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("role", role)
                .toString();
    }
}
