package com.innov.workflow.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Column(unique = true)
    private String name;

    @Lob
    private String description;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    private List<Role> roles;
}
