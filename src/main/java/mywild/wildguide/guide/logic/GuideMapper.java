package mywild.wildguide.guide.logic;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import mywild.wildguide.framework.logic.BaseMapper;
import mywild.wildguide.guide.data.GuideEntity;
import mywild.wildguide.guide.web.Guide;
import mywild.wildguide.guide.web.GuideBase;

@Mapper
public interface GuideMapper extends BaseMapper {

    GuideMapper INSTANCE = Mappers.getMapper(GuideMapper.class);

    @Mapping(target = "name", expression = "java(trim(dto.getName()))")
    public GuideEntity dtoToEntity(Guide dto);

    @Mapping(target = "name", expression = "java(trim(dto.getName()))")
    public GuideEntity dtoToExistingEntity(@MappingTarget GuideEntity entity, GuideBase dto);

    public Guide entityToDto(GuideEntity entity);

    public Guide baseDtoToFullDto(GuideBase superDto);

}
