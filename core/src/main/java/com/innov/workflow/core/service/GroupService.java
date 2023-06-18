package com.innov.workflow.core.service;

import com.innov.workflow.core.domain.entity.Group;
import com.innov.workflow.core.domain.repository.GroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;


    public List<Group> getAllRoles() {
        return groupRepository.findAll();
    }

    public Group getRoleById(Long id) {
        return groupRepository.findById(id).orElse(null);
    }

    public Group createRole(Group group) {
        return groupRepository.save(group);
    }

    public Group updateRole(Long id, Group group) {
        Group existingGroup = groupRepository.findById(id).orElse(null);
        if (existingGroup == null) {
            return null;
        }
        existingGroup.setName(group.getName());
        existingGroup.setDescription(group.getDescription());
        // set other properties as needed
        return groupRepository.save(existingGroup);
    }

    public void deleteRole(Long id) {
        groupRepository.deleteById(id);
    }
}
