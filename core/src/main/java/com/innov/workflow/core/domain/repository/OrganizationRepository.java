package com.innov.workflow.core.domain.repository;

import com.innov.workflow.core.domain.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    List<Organization> findAllByName(String name);
}
