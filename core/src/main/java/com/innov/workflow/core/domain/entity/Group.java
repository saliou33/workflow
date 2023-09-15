package com.innov.workflow.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "CR_GROUP")
@NoArgsConstructor
@AllArgsConstructor
public class Group extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Tag tag;

    @Column(nullable = false)
    private String name;
    @Lob
    private String description;
    @JsonIgnore
    @ManyToOne
    private Organization organization;
}
