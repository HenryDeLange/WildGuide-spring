package mywild.wildguide.domain.entry.logic;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import mywild.wildguide.domain.entry.data.EntryEntity;
import mywild.wildguide.domain.entry.web.Entry;
import mywild.wildguide.domain.entry.web.EntryBase;
import mywild.wildguide.framework.logic.BaseMapper;

@Mapper
public interface EntryMapper extends BaseMapper {

    EntryMapper INSTANCE = Mappers.getMapper(EntryMapper.class);

    @Mapping(target = "name", expression = "java(trim(dto.getName()))")
    @Mapping(target = "scientificName", expression = "java(trim(dto.getScientificName()))")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    public EntryEntity dtoToEntity(Entry dto);

    @Mapping(target = "name", expression = "java(trim(dto.getName()))")
    @Mapping(target = "scientificName", expression = "java(trim(dto.getScientificName()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "guideId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    public EntryEntity dtoToExistingEntity(@MappingTarget EntryEntity entity, EntryBase dto);

    public Entry entityToDto(EntryEntity entity);

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "guideId", source = "guideId")
    public Entry baseDtoToFullDto(EntryBase superDto, long guideId);

}
