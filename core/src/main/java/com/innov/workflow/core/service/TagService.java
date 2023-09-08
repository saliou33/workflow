package com.innov.workflow.core.service;


import com.innov.workflow.core.domain.entity.Tag;
import com.innov.workflow.core.domain.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TagService {

    private final TagRepository tagRepository;


    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Page<Tag> getAllTags(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return tagRepository.findAll(pageable);
    }

    public Tag getTagById(Long id) {
        return tagRepository.findById(id).orElse(null);
    }

    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    public Tag updateTag(Long id, Tag tag) {
        Tag existingTag = tagRepository.findById(id).orElse(null);
        if (existingTag == null) {
            return null;
        }
        existingTag.setName(tag.getName());
        // set other properties as needed
        return tagRepository.save(existingTag);
    }

    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }
}
