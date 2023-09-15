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

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final GroupRepository groupRepository;

    public Page<Organization> getOrganizations(String[] fieldNames, String[] searchTerms, String[] notFieldNames, String[] notValues, Pageable pageable) {
        SearchSpecification<Organization> specification = new SearchSpecification<>();
        return organizationRepository.findAll(specification.searchByFields(fieldNames, searchTerms, notFieldNames, notValues), pageable);
    }

    public Organization getOrganizationById(Long id) {
        return organizationRepository.findById(id).orElse(null);
    }

    public Organization createOrganization(Organization organization) {
        return organizationRepository.save(organization);
    }

    public Organization updateOrganization(Long id, Organization organization) {
        Organization existingOrganization = organizationRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Organization not found with id" + id));

        existingOrganization.setName(organization.getName());
        existingOrganization.setDescription(organization.getDescription());
        return organizationRepository.save(existingOrganization);
    }

    public void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }

    public Organization addGroup(Long orgId, Group group) {
        Organization organization = organizationRepository.findById(orgId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Organization not found with id" + orgId));

        if (group != null) {
            group.setOrganization(organization);
            groupRepository.save(group);
        }

        return organization;
    }
}
