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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations")
//@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;
    private final OrganizationMapper orgMapper;

    private final GroupMapper groupMapper;

    @GetMapping
    public ResponseEntity getOrganizations(
            @RequestParam(name = "f", required = false) String[] fieldNames,
            @RequestParam(name = "s", required = false) String[] searchTerms,
            @RequestParam(name = "n", required = false) String[] notFieldNames,
            @RequestParam(name = "nv", required = false) String[] notValues,
            Pageable pageable
    ) {
        Page<Organization> data = organizationService.getOrganizations(fieldNames, searchTerms, notFieldNames, notValues, pageable);
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
        return ApiResponse.created("organization created", organization);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateOrganization(@PathVariable Long id, @RequestBody OrganizationDto organizationDto) {
        Organization data = orgMapper.mapFromDto(organizationDto);
        Organization organization = organizationService.updateOrganization(id, data);
        return ApiResponse.success("organization updated", organization);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ApiResponse.success("organization deleted");
    }

    @PostMapping("/{id}/groups")
    public ResponseEntity addGroup(@PathVariable Long id, @RequestBody GroupDto groupDto) {
        Group group = groupMapper.mapFromDto(groupDto);
        organizationService.addGroup(id, group);
        return ApiResponse.created("group added", group);
    }
}
