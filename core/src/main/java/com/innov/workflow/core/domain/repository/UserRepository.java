package com.innov.workflow.core.domain.repository;

import com.innov.workflow.core.domain.entity.Group;
import com.innov.workflow.core.domain.entity.Organization;
import com.innov.workflow.core.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Page<User> findAllByGroups(Group group, Pageable pageable);

    List<User> findUsersByUsernameLike(String username);

    Page<User> findAllByOrganization(Organization organization, Pageable pageable);

}
