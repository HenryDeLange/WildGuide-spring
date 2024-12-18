package mywild.wildguide.user.logic;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import mywild.wildguide.user.data.UserEntity;
import mywild.wildguide.user.web.User;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    public UserEntity dtoToEntity(User dto);

    public User entityToDto(UserEntity entity);

}
