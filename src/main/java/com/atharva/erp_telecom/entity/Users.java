package com.atharva.erp_telecom.entity;


import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long userId;

    @Column(name = "user_name",nullable = false, unique = true,length = 100)
    private String username;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "user_first_name",nullable = false)
    private String userFirstName;

    @Column(name = "user_last_name",nullable = false)
    private String userLastName;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "enabled",nullable = false)
    private Boolean enabled;

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
}
