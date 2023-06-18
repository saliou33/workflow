package com.innov.workflow.core.domain.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "CR_SYS_SETTING")
public class SysSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
