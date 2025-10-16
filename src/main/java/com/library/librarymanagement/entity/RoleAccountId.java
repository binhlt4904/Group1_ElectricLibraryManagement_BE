package com.library.librarymanagement.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RoleAccountId implements Serializable {
    private String roleName;
    private Long accountId;

    public RoleAccountId() {}

    public RoleAccountId(String roleName, Long accountId) {
        this.roleName = roleName;
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleAccountId that = (RoleAccountId) o;
        return Objects.equals(roleName, that.roleName) && Objects.equals(accountId, that.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleName, accountId);
    }
}
