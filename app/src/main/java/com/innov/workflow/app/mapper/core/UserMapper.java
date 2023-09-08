package com.innov.workflow.app.mapper.core;

import com.innov.workflow.app.dto.core.UserDto;
import com.innov.workflow.core.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @ValueMapping(source = "userId", target = "id")
    public abstract UserDto mapToDto(User user);

    @ValueMapping(source = "userId", target = "id")
    public abstract List<UserDto> mapToDtoList(List<User> userList);

    @ValueMapping(source = "id", target = "userId")
    public abstract User mapFromDto(UserDto userDTO);
}
