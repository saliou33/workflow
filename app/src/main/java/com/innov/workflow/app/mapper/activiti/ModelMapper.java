package com.innov.workflow.app.mapper.activiti;


import com.innov.workflow.app.dto.activiti.ModelDto;
import org.activiti.engine.repository.Model;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ModelMapper {

    public abstract ModelDto mapToDto(Model model);

//    @Mapping(target = "id", source="m.modelId")
//    @Mapping(target = "owner", source="m.owner")
//    public abstract List<ModelDto> mapToDtoList (List<Model> model,
//                                                com.innov.workflow.core.domain.entity.activiti.Model m);
//    public abstract List<ModelDto> mapToDtoList (List<Model> model);
}
