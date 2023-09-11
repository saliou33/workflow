package com.innov.workflow.core.service;

import com.innov.workflow.core.domain.entity.Group;
import com.innov.workflow.core.domain.entity.Organization;
import com.innov.workflow.core.domain.repository.GroupRepository;
import com.innov.workflow.core.domain.repository.OrganizationRepository;
import com.innov.workflow.core.exception.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final GroupRepository groupRepository;


    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }


    public Page<Organization> getAllOrganizations(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return organizationRepository.findAll(pageable);
    }

    public Organization getOrganizationById(Long id) {
        return organizationRepository.findById(id).orElse(null);
    }

    public Organization createOrganization(Organization organization) {
        return organizationRepository.save(organization);
    }

    public Organization updateOrganization(Long id, Organization organization) {
        Organization existingOrganization = organizationRepository.findById(id).orElse(null);
        if (existingOrganization == null) {
            return null;
        }
        existingOrganization.setName(organization.getName());
        existingOrganization.setDescription(organization.getDescription());
        // set other properties as needed
        return organizationRepository.save(existingOrganization);
    }

    public void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }

    public Organization createGroup(Long orgId, Group group) {
        Organization organization = organizationRepository.findById(orgId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Organization not found with id" + orgId));

        Group group1 = groupRepository.findByName(group.getName());

        if (group1 == null) {
            group1 = groupRepository.save(group);
        }

        if (!organization.getGroups().contains(group1)) {
            organization.getGroups().add(group1);
            organization = organizationRepository.save(organization);
        }

        return organization;
    }
}
