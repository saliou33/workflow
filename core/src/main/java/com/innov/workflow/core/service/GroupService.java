package com.innov.workflow.core.service;

import com.innov.workflow.core.domain.entity.Group;
import com.innov.workflow.core.domain.entity.Organization;
import com.innov.workflow.core.domain.repository.GroupRepository;
import com.innov.workflow.core.domain.repository.OrganizationRepository;
import com.innov.workflow.core.exception.ApiException;
import com.innov.workflow.core.specification.SearchSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    private final OrganizationRepository organizationRepository;

    public Page<Group> getGroups(String[] fieldNames, String[] searchTerms, String[] notFieldNames, String[] notValues, Pageable pageable) {
        SearchSpecification<Group> specification = new SearchSpecification<>();
        return groupRepository.findAll(specification.searchByFields(fieldNames, searchTerms, notFieldNames, notValues), pageable);
    }

    public Group getGroupById(Long id) {
        return groupRepository.findById(id).orElse(null);
    }

    public Page<Group> getGroupsByOrganization(Long orgId, Pageable pageable) {
        Organization organization = organizationRepository.findById(orgId).orElseThrow(() -> new ApiException(
                HttpStatus.BAD_REQUEST, "organization not found"
        ));

        return groupRepository.findAllByOrganization(organization, pageable);
    }

    public List<Group> getGroupsByNameLike(String pattern) {
        return groupRepository.findAllByNameLike(pattern);
    }

    public List<Group> getGroupsByNameLikeAndOrganization(String pattern, Organization organization) {
        return groupRepository.findAllByNameLikeAndOrganization(pattern, organization);
    }


    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }

    public Group updateGroup(Long id, Group group) {
        Group existingGroup = groupRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "group not found"));
        existingGroup.setName(group.getName());
        existingGroup.setDescription(group.getDescription());
        // set other properties as needed
        return groupRepository.save(existingGroup);
    }

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }
}
