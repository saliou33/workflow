package com.innov.workflow.core.domain.repository;

import com.innov.workflow.core.domain.entity.Group;
import com.innov.workflow.core.domain.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {
    Group findByName(String name);

    Page<Group> findAllByOrganization(Organization organization, Pageable pageable);

    List<Group> findAllByNameLikeAndOrganization(String pattern, Organization organization);

    List<Group> findAllByNameLike(String pattern);

}
