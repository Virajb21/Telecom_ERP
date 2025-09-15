package com.atharva.erp_telecom.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.*;
import java.util.Collections.*;

// Good practice to have roles to be stored like: ROLE_ADMIN, ROLE_USER, etc.
@Entity
@Table(name="roles")
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(nullable = false, unique = true, length = 50)
    private String roleName;

    // Added JSON ignore to avoid infinite recursion in serialization/deserialization.
    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<Users> users = new HashSet<>();

    // As a good practice - added No-args constructor which will be used by JPA (15/09/2025).
    public Roles(){}

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String name) {
        this.roleName = name;
    }

    public Set<Users> getUsers() {
        return users;
    }

    public void setUsers(Set<Users> users) {
        this.users = users;
    }
}
