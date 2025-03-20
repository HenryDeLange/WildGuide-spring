package mywild.wildguide.domain.guide.logic;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import mywild.wildguide.framework.logic.BaseMapper;
import mywild.wildguide.domain.guide.data.GuideEntity;
import mywild.wildguide.domain.guide.data.GuideEntityExtended;
import mywild.wildguide.domain.guide.web.Guide;
import mywild.wildguide.domain.guide.web.GuideBase;

@Mapper
public interface GuideMapper extends BaseMapper {

    GuideMapper INSTANCE = Mappers.getMapper(GuideMapper.class);

    @Mapping(target = "name", expression = "java(trim(dto.getName()))")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    public GuideEntity dtoToEntity(Guide dto);

    @Mapping(target = "name", expression = "java(trim(dto.getName()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    public GuideEntity dtoToExistingEntity(@MappingTarget GuideEntity entity, GuideBase dto);

    @Mapping(target = "starredByUser", constant = "false")
    public Guide entityToDto(GuideEntity entity);

    @Mapping(target = "starredByUser", source = "starredByUser")
    public Guide entityToDto(GuideEntity entity, boolean starredByUser);

    public Guide entityToDto(GuideEntityExtended entity);

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "starredByUser", constant = "false")
    public Guide baseDtoToFullDto(GuideBase superDto);

}
