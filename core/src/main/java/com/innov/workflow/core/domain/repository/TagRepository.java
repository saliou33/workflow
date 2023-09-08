package com.innov.workflow.core.domain.repository;

import com.innov.workflow.core.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
