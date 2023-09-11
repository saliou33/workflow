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
@RequestMapping("/api/groups")
@AllArgsConstructor
public class GroupController {
    private final GroupService groupService;

    private final GroupMapper groupMapper;

    @GetMapping
    public ResponseEntity getAllGroups() {
        return ApiResponse.success(groupService.getAllGroups());
    }

    @GetMapping("/{id}")
    public ResponseEntity getGroupById(@PathVariable Long id) {
        return ApiResponse.success(groupService.getGroupById(id));
    }

    @PostMapping
    public ResponseEntity createGroup(@RequestBody GroupDto groupDTO) {
        Group data = groupMapper.mapFromDto(groupDTO);
        Group group = groupService.createGroup(data);
        return ApiResponse.created("Group créer", group);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateGroup(@PathVariable Long id, @RequestBody GroupDto groupDTO) {
        Group data = groupMapper.mapFromDto(groupDTO);
        Group group = groupService.updateGroup(id, data);
        return ApiResponse.success("Group modifier", group);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ApiResponse.success("Group supprimer");
    }
}

