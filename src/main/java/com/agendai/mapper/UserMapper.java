package com.agendai.mapper;

import com.agendai.dto.RegisterRequest;
import com.agendai.dto.UserResponse;
import com.agendai.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ProfessionalMapper.class})
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "professional", ignore = true)
    @Mapping(target = "customerBookings", ignore = true)
    @Mapping(target = "ownedEstablishments", ignore = true)
    User toEntity(RegisterRequest request);

    @Mapping(target = "professionalData", source = "professional")
    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateUserFromRequest(RegisterRequest request, @MappingTarget User user);
}
