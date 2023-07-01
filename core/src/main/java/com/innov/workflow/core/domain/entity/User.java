package com.innov.workflow.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String lastName;
    private String firstName;
    private String avatar;
    @Email
    @Column(unique = true)
    private String email;
    @JsonIgnore
    private String password;
    private String tel;
    private boolean enabled;
    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Group> groups;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<SysRole> sysRoles;


    public User(String username, String email, String password) {
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
}
