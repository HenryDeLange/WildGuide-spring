package mywild.wildguide.guide.logic;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import mywild.wildguide.guide.data.GuideEntity;
import mywild.wildguide.guide.web.Guide;
import mywild.wildguide.guide.web.GuideBase;

@Mapper
public interface GuideMapper {

    GuideMapper INSTANCE = Mappers.getMapper(GuideMapper.class);

    public GuideEntity dtoToEntity(Guide dto);

    public GuideEntity dtoToExistingEntity(@MappingTarget GuideEntity entity, GuideBase dto);

    public Guide entityToDto(GuideEntity entity);

    public Guide baseDtoToFullDto(GuideBase superDto);

}
