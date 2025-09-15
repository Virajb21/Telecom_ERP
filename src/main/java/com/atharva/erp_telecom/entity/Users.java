package com.atharva.erp_telecom.entity;


import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name="users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Changed from GenerationType.SEQUENCE to GenerationType.IDENTITY - better suited for MYSQL.
    private Long userId;

    @Column(name = "user_name",nullable = false, unique = true,length = 100)
    private String userName;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "user_first_name",nullable = false)
    private String userFirstName;

    @Column(name = "user_last_name",nullable = false)
    private String userLastName;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "enabled")
    private Boolean enabled = true; // Made the default value as true

    @CreatedDate
    @Column(name = "create_time",nullable = false,updatable = false,insertable = false,
    columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createTime;

    @LastModifiedDate
    @Column(name = "updated_time",nullable = false,insertable = false)
    private LocalDateTime updatedTime;

    // (Best practice to use this) --> This is our join table which stores a many-to-many mapping for all the users and corresponding roles.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles_join",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )
    private Set<Roles> roles = new HashSet<>();

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserFirstName() {
        return userFirstName;
    }
    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }
    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Set<Roles> getRoles() {
        return roles;
    }
    // Reverted back to the older version for setter.
    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
    }
}
