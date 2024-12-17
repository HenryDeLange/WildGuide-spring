package mywild.wildguide.guide_entry.logic;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import mywild.wildguide.guide_entry.data.EntryEntity;
import mywild.wildguide.guide_entry.web.Entry;
import mywild.wildguide.guide_entry.web.EntryBase;

@Mapper
public interface EntryMapper {

    EntryMapper INSTANCE = Mappers.getMapper(EntryMapper.class);

    public EntryEntity dtoToEntity(Entry dto);

    public EntryEntity dtoToExistingEntity(@MappingTarget EntryEntity entity, EntryBase dto);

    public Entry entityToDto(EntryEntity entity);

    @Mapping(target = "guideId", source = "guideId")
    public Entry baseDtoToFullDto(EntryBase superDto, long guideId);

}
