package mywild.wildguide.user.logic;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import mywild.wildguide.user.data.UserEntity;
import mywild.wildguide.user.web.User;
import mywild.wildguide.user.web.UserInfo;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    public UserEntity dtoToEntity(User dto);

    public UserInfo entityToDto(UserEntity entity);

}
