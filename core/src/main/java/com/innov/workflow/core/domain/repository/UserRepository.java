package com.innov.workflow.core.domain.repository;

import com.innov.workflow.core.domain.entity.Group;
import com.innov.workflow.core.domain.entity.Organization;
import com.innov.workflow.core.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<User> findAllByGroups(Group group);

    List<User> findUsersByUsernameLike(String p);

    List<User> findAllByOrganization(Organization organization);


}
