package com.innov.workflow.app.controller.core;

import com.innov.workflow.app.dto.PaginationDto;
import com.innov.workflow.app.dto.core.TagDto;
import com.innov.workflow.app.mapper.core.TagMapper;
import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.domain.entity.Tag;
import com.innov.workflow.core.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@AllArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    @GetMapping
    public ResponseEntity getAllTagTags() {
        return ApiResponse.success(tagService.getAllTags());
    }

    @PostMapping("/pages")
    public ResponseEntity getAllTagTagsByPage(@RequestBody PaginationDto p) {
        List<Tag> tags = tagService.getAllTags(p.getPageNumber(), p.getPageSize()).toList();

        return ApiResponse.success(tags);
    }

    @GetMapping("/{id}")
    public ResponseEntity getTagById(@PathVariable Long id) {
        return ApiResponse.success(tagService.getTagById(id));
    }

    @PostMapping
    public ResponseEntity createTag(@RequestBody TagDto tagDTO) {
        Tag data = tagMapper.mapFromDto(tagDTO);
        Tag tag = tagService.createTag(data);
        return ApiResponse.created("tag créer", tag);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateTag(@PathVariable Long id, @RequestBody TagDto tagDTO) {
        Tag data = tagMapper.mapFromDto(tagDTO);
        Tag tag = tagService.updateTag(id, data);
        return ApiResponse.success("tag modifier", tag);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ApiResponse.success("tag supprimer");
    }
}

