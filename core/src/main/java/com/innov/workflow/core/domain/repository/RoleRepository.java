package com.innov.workflow.core.domain.repository;

import com.innov.workflow.core.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
