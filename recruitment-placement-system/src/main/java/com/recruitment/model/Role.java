package com.recruitment.model;

import java.time.LocalDateTime;

public class Role {
    private Integer roleId;
    private String roleCode;
    private String roleName;
    private boolean active;
    private LocalDateTime createdAt;

    public Role() {
        this.active = true;
    }

    public Role(Integer roleId, String roleCode, String roleName, boolean active, LocalDateTime createdAt) {
        this.roleId = roleId;
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s)", roleId, roleName, roleCode);
    }
}
