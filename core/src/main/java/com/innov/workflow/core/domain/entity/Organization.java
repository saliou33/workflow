package com.innov.workflow.core.domain.entity;

import com.innov.workflow.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Organization extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orgId;

    @ManyToMany
    @JoinTable(name = "organization_role",
        joinColumns =  @JoinColumn(name = "roleId"),
        inverseJoinColumns = @JoinColumn(name = "orgId"))
    private List<Role> roles;
}
