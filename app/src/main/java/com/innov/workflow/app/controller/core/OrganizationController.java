package com.innov.workflow.app.controller.core;

import com.innov.workflow.app.dto.core.OrganizationDTO;
import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.domain.entity.Organization;
import com.innov.workflow.core.service.OrganizationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
//@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping
    public ResponseEntity getAllOrganizations() {
        return ApiResponse.success(organizationService.getAllOrganizations());
    }

    @GetMapping("/{id}")
    public ResponseEntity getOrganizationById(@PathVariable Long id) {
        return ApiResponse.success(organizationService.getOrganizationById(id));
    }

    @PostMapping("/pages")
    public ResponseEntity getAllOrganizationssByPage(int pageNumber, int pageSize) {
        List<Organization> data = organizationService.getAllOrganizations(pageNumber, pageSize).toList();
        return ApiResponse.success(data);
    }

    @PostMapping
    public ResponseEntity createOrganization(@RequestBody OrganizationDTO organizationDTO) {
        Organization organization = organizationService.createOrganization(organizationDTO.toEntity());
        return ApiResponse.created("organisation créer", organization);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateOrganization(@PathVariable Long id, @RequestBody OrganizationDTO organizationDTO) {
        Organization organization = organizationService.updateOrganization(id, organizationDTO.toEntity());
        return ApiResponse.success("organisation modifier", organization);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ApiResponse.success("organisation supprimer");
    }

}
