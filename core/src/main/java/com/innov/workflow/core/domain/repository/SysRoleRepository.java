package com.innov.workflow.core.domain.repository;

import com.innov.workflow.core.domain.entity.EnumSysRole;
import com.innov.workflow.core.domain.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysRoleRepository extends JpaRepository<SysRole, Long> {
    SysRole findByName(EnumSysRole name);
}
