package ohio.rizz.homeappliancestore.mappers;

import ohio.rizz.homeappliancestore.dto.UserCreateDto;
import ohio.rizz.homeappliancestore.dto.UserDto;
import ohio.rizz.homeappliancestore.entities.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(userDto.getPassword()))")
    @Mapping(target = "enabled", constant = "true")
    User toEntity(UserCreateDto userDto, @Context PasswordEncoder passwordEncoder);
}