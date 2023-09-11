package com.innov.workflow.core.domain.entity;


import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "CR_SYS_ROLE")
@Data
public class SysRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EnumSysRole name;
}
