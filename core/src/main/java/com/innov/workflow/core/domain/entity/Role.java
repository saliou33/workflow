package com.innov.workflow.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.innov.workflow.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @ManyToOne
    @JoinColumn(name = "tagId")
    private RoleTag tag;

    private String name;

    @Lob
    private String description;

    @ManyToOne
    @JoinColumn(name = "orgId")
    @JsonIgnore
    private Organization organization;
}
