package com.innov.workflow.app.controller.core;

import com.innov.workflow.app.dto.core.GroupDto;
import com.innov.workflow.app.dto.core.OrganizationDto;
import com.innov.workflow.app.mapper.core.GroupMapper;
import com.innov.workflow.app.mapper.core.OrganizationMapper;
import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.domain.entity.Group;
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
    private final OrganizationMapper orgMapper;

    private final GroupMapper groupeMapper;

    @GetMapping
    public ResponseEntity getAllOrganizations() {
        List<OrganizationDto> data = orgMapper.
                mapToDtoList(organizationService.getAllOrganizations());
        return ApiResponse.success(data);
    }

    @GetMapping("/{id}")
    public ResponseEntity getOrganizationById(@PathVariable Long id) {
        OrganizationDto organization = orgMapper.
                mapToDto(organizationService.getOrganizationById(id));
        return ApiResponse.success(organization);
    }


    @PostMapping
    public ResponseEntity createOrganization(@RequestBody OrganizationDto organizationDto) {
        Organization data = orgMapper.mapFromDto(organizationDto);
        Organization organization = organizationService.createOrganization(data);
        return ApiResponse.created("organisation créer", organization);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateOrganization(@PathVariable Long id, @RequestBody OrganizationDto organizationDto) {
        Organization data = orgMapper.mapFromDto(organizationDto);
        Organization organization = organizationService.updateOrganization(id, data);
        return ApiResponse.success("organisation modifier", organization);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ApiResponse.success("organisation supprimer");
    }

    @PostMapping("/{id}/groups")
    public ResponseEntity createGroup(@PathVariable Long id, @RequestBody GroupDto groupDto) {
        Group group = groupeMapper.mapFromDto(groupDto);
        organizationService.createGroup(id, group);
        return ApiResponse.created("groupe créer", group);
    }
}
