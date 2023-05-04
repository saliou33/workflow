package com.innov.workflow.core.service;

import com.innov.workflow.core.domain.entity.Organization;
import com.innov.workflow.core.domain.entity.Role;
import com.innov.workflow.core.domain.repository.OrganizationRepository;
import com.innov.workflow.core.domain.repository.RoleRepository;
import com.innov.workflow.core.exception.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final RoleRepository roleRepository;

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

    public Organization addRole(Long orgId, Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Role not found with id" + roleId));
        Organization organization = organizationRepository.findById(orgId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Organization not found with id" + orgId));

        if (!organization.getRoles().contains(role)) {
            organization.getRoles().add(role);
            return organizationRepository.save(organization);
        }

        return null;
    }
}
