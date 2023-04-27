package com.minkyu.myproject.user.domain;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.regex.Pattern.matches;

@Embeddable
@Access(AccessType.FIELD)
public class Email {

    @Column(name="email", length = 50, unique = true, nullable = false)
    private String address;

    public Email(String address){
        checkArgument(address != null, "address must be provided");
        checkArgument(address.length() >= 4 && address.length() <= 50,
                "address length must be between 4 to 50 characters");
        checkArgument(checkAddress(address), "Invalid email address:" + address);

        this.address=address;
    }

    protected Email() {

    }

    private static boolean checkAddress(String address) {
        return matches("[\\w~\\-.+]+@[\\w~\\-]+(\\.[\\w~\\-]+)+", address);
    }

    private String[] getSplitAddress() {
        return address.split("@");
    }

    public String getName() {
        String[] tokens = getSplitAddress();
        return tokens[0];
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Email email = (Email) o;
        return Objects.equals(address, email.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("address", address)
                .toString();
    }

}
