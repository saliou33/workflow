package com.innov.workflow.core.service;


import com.innov.workflow.core.domain.entity.RoleTag;
import com.innov.workflow.core.domain.repository.RoleTagRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RoleTagService {

    private final RoleTagRepository roleTagRepository;


    public List<RoleTag> getAllRoleTags() {
        return roleTagRepository.findAll();
    }

    public Page<RoleTag> getAllRoleTags(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return roleTagRepository.findAll(pageable);
    }

    public RoleTag getRoleTagById(Long id) {
        return roleTagRepository.findById(id).orElse(null);
    }

    public RoleTag createRoleTag(RoleTag roleTag) {
        return roleTagRepository.save(roleTag);
    }

    public RoleTag updateRoleTag(Long id, RoleTag roleTag) {
        RoleTag existingRole = roleTagRepository.findById(id).orElse(null);
        if (existingRole == null) {
            return null;
        }
        existingRole.setName(roleTag.getName());
        // set other properties as needed
        return roleTagRepository.save(existingRole);
    }

    public void deleteRoleTag(Long id) {
        roleTagRepository.deleteById(id);
    }
}
