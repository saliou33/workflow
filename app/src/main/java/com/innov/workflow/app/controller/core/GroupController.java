package com.innov.workflow.app.controller.core;

import com.innov.workflow.app.dto.core.GroupDto;
import com.innov.workflow.app.mapper.core.GroupMapper;
import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.domain.entity.Group;
import com.innov.workflow.core.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@AllArgsConstructor
public class GroupController {
    private final GroupService groupService;

    private final GroupMapper groupMapper;

    @GetMapping
    public ResponseEntity getGroups(
            @RequestParam(name = "f", required = false) String[] fieldNames,
            @RequestParam(name = "s", required = false) String[] searchTerms,
            @RequestParam(name = "n", required = false) String[] notFieldNames,
            @RequestParam(name = "nv", required = false) String[] notValues,
            Pageable pageable
    ) {
        Page<Group> data = groupService.getGroups(fieldNames, searchTerms, notFieldNames, notValues, pageable);
        return ApiResponse.success(data);
    }

    @GetMapping("/{id}")
    public ResponseEntity getGroupById(@PathVariable Long id) {
        return ApiResponse.success(groupService.getGroupById(id));
    }

    @GetMapping("/organization/{id}")
    public ResponseEntity getGroupByOrganization(@PathVariable Long id, Pageable pageable) {
        return ApiResponse.success(groupService.getGroupsByOrganization(id, pageable));
    }

    @PostMapping
    public ResponseEntity createGroup(@RequestBody GroupDto groupDTO) {
        Group data = groupMapper.mapFromDto(groupDTO);
        Group group = groupService.createGroup(data);
        return ApiResponse.created("group created", group);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateGroup(@PathVariable Long id, @RequestBody GroupDto groupDTO) {
        Group data = groupMapper.mapFromDto(groupDTO);
        Group group = groupService.updateGroup(id, data);
        return ApiResponse.success("group updated", group);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ApiResponse.success("group deleted");
    }
}

