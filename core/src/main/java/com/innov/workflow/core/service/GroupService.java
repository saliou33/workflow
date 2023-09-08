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


    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupById(Long id) {
        return groupRepository.findById(id).orElse(null);
    }

    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }

    public Group updateGroup(Long id, Group group) {
        Group existingGroup = groupRepository.findById(id).orElse(null);
        if (existingGroup == null) {
            return null;
        }
        existingGroup.setName(group.getName());
        existingGroup.setDescription(group.getDescription());
        // set other properties as needed
        return groupRepository.save(existingGroup);
    }

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }
}
