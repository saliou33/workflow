package com.innov.workflow.app.controller.core;

import com.innov.workflow.app.dto.PaginationDTO;
import com.innov.workflow.app.dto.core.RoleTagDTO;
import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.domain.entity.RoleTag;
import com.innov.workflow.core.service.RoleTagService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@AllArgsConstructor
public class RoleTagController {

    private final RoleTagService roleTagService;

    @GetMapping
    public ResponseEntity getAllRoleTags() {
        return ApiResponse.success(roleTagService.getAllRoleTags());
    }

    @PostMapping("/pages")
    public ResponseEntity getAllRoleTagsByPage(@RequestBody PaginationDTO p) {
        List<RoleTag> tags = roleTagService.getAllRoleTags(p.getPageNumber(), p.getPageSize()).toList();

        return ApiResponse.success(tags);
    }

    @GetMapping("/{id}")
    public ResponseEntity getRoleById(@PathVariable Long id) {
        return ApiResponse.success(roleTagService.getRoleTagById(id));
    }

    @PostMapping
    public ResponseEntity createRole(@RequestBody RoleTagDTO roleTagDTO) {
        RoleTag tag = roleTagService.createRoleTag(roleTagDTO.toEntity());
        return ApiResponse.created("tag créer", tag);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateRole(@PathVariable Long id, @RequestBody RoleTagDTO roleTagDTO) {
        RoleTag tag = roleTagService.updateRoleTag(id, roleTagDTO.toEntity());
        return ApiResponse.success("tag modifier", tag);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteRole(@PathVariable Long id) {
        roleTagService.deleteRoleTag(id);
        return ApiResponse.success("tag supprimer");
    }
}

