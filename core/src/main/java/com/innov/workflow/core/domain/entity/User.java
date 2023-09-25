package com.innov.workflow.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.innov.workflow.core.domain.entity.auth.RefreshToken;
import com.innov.workflow.core.domain.entity.auth.VerificationToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.List;

@Entity
@Table(name = "CR_USER")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(unique = true)
    private String username;
    private String id;
    private String fullName;

    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String firstName;
    private String avatar;
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    @JsonIgnore
    @Column(nullable = false)
    private String password;
    private String tel;
    private boolean enabled;
    @ManyToOne(fetch = FetchType.EAGER)
    private Organization organization;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Group> groups;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<SysRole> roles;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshToken refreshToken;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private VerificationToken verificationToken;


    public User(String username, String email, String password) {
        // for activiti engine id of user is the username
        this.id = username;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
        this.id = username;
    }


    public void setFullName() {
        this.fullName = this.firstName + " " + this.lastName;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public String getId() {
        return getUsername();
    }
}
