package com.innov.workflow.app.controller.core;

import com.innov.workflow.app.dto.core.GroupDto;
import com.innov.workflow.app.mapper.core.GroupMapper;
import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.domain.entity.Group;
import com.innov.workflow.core.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@AllArgsConstructor
public class GroupController {
    private final GroupService groupService;

    private final GroupMapper groupMapper;

    @GetMapping
    public ResponseEntity getAllRoles() {
        return ApiResponse.success(groupService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity getRoleById(@PathVariable Long id) {
        return ApiResponse.success(groupService.getRoleById(id));
    }

    @PostMapping
    public ResponseEntity createRole(@RequestBody GroupDto groupDTO) {
        Group data = groupMapper.mapFromDto(groupDTO);
        Group group = groupService.createRole(data);
        return ApiResponse.created("role créer", group);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateRole(@PathVariable Long id, @RequestBody GroupDto groupDTO) {
        Group data = groupMapper.mapFromDto(groupDTO);
        Group group = groupService.updateRole(id, data);
        return ApiResponse.success("role modifier", group);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteRole(@PathVariable Long id) {
        groupService.deleteRole(id);
        return ApiResponse.success("role supprimer");
    }
}

