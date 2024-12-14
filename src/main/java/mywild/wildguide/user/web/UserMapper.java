package mywild.wildguide.user.web;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import mywild.wildguide.user.data.UserEntity;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    public UserEntity dtoToEntity(User dto);

    public User entityToDto(UserEntity entity);

}
