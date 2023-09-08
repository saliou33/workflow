package com.innov.workflow.app.mapper.core;

import com.innov.workflow.app.dto.core.TagDto;
import com.innov.workflow.core.domain.entity.Tag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TagMapper {

    public abstract TagDto mapToDto(Tag tag);

    public abstract List<TagDto> mapToDtoList(List<Tag> tagList);

    public abstract Tag mapFromDto(TagDto tagDto);
}
