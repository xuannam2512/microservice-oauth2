package com.microservice.oauth2.domain.mapper;

import com.microservice.oauth2.domain.User;
import com.microservice.oauth2.domain.bean.UserDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(UserDto userDto);

    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "roleName", source = "role.name")
    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void update(@MappingTarget User user, UserDto userDto);
}
