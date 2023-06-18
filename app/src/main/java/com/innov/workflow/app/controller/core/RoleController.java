package com.innov.workflow.app.controller.core;

import com.innov.workflow.app.dto.PaginationDto;
import com.innov.workflow.app.dto.core.RoleDto;
import com.innov.workflow.app.mapper.core.RoleMapper;
import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.domain.entity.Role;
import com.innov.workflow.core.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@AllArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @GetMapping
    public ResponseEntity getAllRoleTags() {
        return ApiResponse.success(roleService.getAllRoles());
    }

    @PostMapping("/pages")
    public ResponseEntity getAllRoleTagsByPage(@RequestBody PaginationDto p) {
        List<Role> tags = roleService.getAllRoles(p.getPageNumber(), p.getPageSize()).toList();

        return ApiResponse.success(tags);
    }

    @GetMapping("/{id}")
    public ResponseEntity getRoleById(@PathVariable Long id) {
        return ApiResponse.success(roleService.getRoleById(id));
    }

    @PostMapping
    public ResponseEntity createRole(@RequestBody RoleDto roleDTO) {
        Role data = roleMapper.mapFromDto(roleDTO);
        Role tag = roleService.createRole(data);
        return ApiResponse.created("tag créer", tag);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateRole(@PathVariable Long id, @RequestBody RoleDto roleDTO) {
        Role data = roleMapper.mapFromDto(roleDTO);
        Role tag = roleService.updateRole(id, data);
        return ApiResponse.success("tag modifier", tag);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.success("tag supprimer");
    }
}

