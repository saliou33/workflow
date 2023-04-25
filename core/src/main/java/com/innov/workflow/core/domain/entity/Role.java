package com.innov.workflow.core.domain.entity;

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
    private String roleKey;
    private String name;
    @ManyToMany(mappedBy = "roles")
    private List<Organization> organizations;
}