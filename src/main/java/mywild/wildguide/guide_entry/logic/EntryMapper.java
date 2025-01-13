package mywild.wildguide.guide_entry.logic;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import mywild.wildguide.framework.logic.BaseMapper;
import mywild.wildguide.guide_entry.data.EntryEntity;
import mywild.wildguide.guide_entry.web.Entry;
import mywild.wildguide.guide_entry.web.EntryBase;

@Mapper
public interface EntryMapper extends BaseMapper {

    EntryMapper INSTANCE = Mappers.getMapper(EntryMapper.class);

    @Mapping(target = "name", expression = "java(trim(dto.getName()))")
    @Mapping(target = "scientificName", expression = "java(trim(dto.getScientificName()))")
    public EntryEntity dtoToEntity(Entry dto);

    @Mapping(target = "name", expression = "java(trim(dto.getName()))")
    @Mapping(target = "scientificName", expression = "java(trim(dto.getScientificName()))")
    public EntryEntity dtoToExistingEntity(@MappingTarget EntryEntity entity, EntryBase dto);

    public Entry entityToDto(EntryEntity entity);

    @Mapping(target = "guideId", source = "guideId")
    public Entry baseDtoToFullDto(EntryBase superDto, long guideId);

}
