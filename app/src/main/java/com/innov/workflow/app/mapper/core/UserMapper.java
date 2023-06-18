package com.innov.workflow.app.mapper.core;

import com.innov.workflow.app.dto.core.UserDto;
import com.innov.workflow.core.domain.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    public abstract UserDto mapToDto(User user);

    public abstract List<UserDto> mapToDtoList(List<User> userList);

    public abstract User mapFromDto(UserDto userDTO);
}
