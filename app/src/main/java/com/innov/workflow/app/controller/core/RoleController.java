package com.innov.workflow.app.controller.core;

import com.innov.workflow.app.dto.PaginationDTO;
import com.innov.workflow.app.dto.core.RoleDTO;
import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.domain.entity.Role;
import com.innov.workflow.core.service.RoleService;
import lombok.AllArgsConstructor;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@AllArgsConstructor
public class RoleController {
    private final RoleService roleService;

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity getAllRoles() {
        return ApiResponse.success(roleService.getAllRoles());
    }

    @PostMapping("/pages")
    public ResponseEntity getAllRolesByPage(@RequestBody PaginationDTO p) {
        List<Role> data = roleService.getAllRoles(p.getPageNumber(), p.getPageSize()).toList();
        
        return ApiResponse.success(data);
    }

    @GetMapping("/{id}")
    public ResponseEntity getRoleById(@PathVariable Long id) {
        return ApiResponse.success(roleService.getRoleById(id));
    }

    @PostMapping
    public ResponseEntity createRole(@RequestBody RoleDTO roleDTO) {
        Role role = roleService.createRole(roleDTO.toEntity());
        return ApiResponse.created("role créer", role);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateRole(@PathVariable Long id, @RequestBody RoleDTO roleDTO) {

        Role role = roleService.updateRole(id, roleDTO.toEntity());
        return ApiResponse.success("role modifier", role);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.success("role supprimer");
    }
}

