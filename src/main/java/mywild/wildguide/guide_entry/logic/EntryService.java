package mywild.wildguide.guide_entry.logic;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import mywild.wildguide.framework.error.ForbiddenException;
import mywild.wildguide.framework.error.NotFoundException;
import mywild.wildguide.framework.web.Paged;
import mywild.wildguide.guide.data.GuideEntity;
import mywild.wildguide.guide.data.GuideMemberLinkRepository;
import mywild.wildguide.guide.data.GuideOwnerLinkRepository;
import mywild.wildguide.guide.data.GuideRepository;
import mywild.wildguide.guide.data.GuideVisibilityType;
import mywild.wildguide.guide_entry.data.EntryEntity;
import mywild.wildguide.guide_entry.data.EntryRepository;
import mywild.wildguide.guide_entry.web.Entry;
import mywild.wildguide.guide_entry.web.EntryBase;

@Validated
@Service
public class EntryService {

    @Value("${mywild.wildguide.page-size}")
    private int pageSize;

    @Autowired
    private EntryRepository repoEntry;

    @Autowired
    private GuideRepository repoGuide;

    @Autowired
    private GuideOwnerLinkRepository repoGuideOwner;

    @Autowired
    private GuideMemberLinkRepository repoGuideMember;

    public @Valid Paged<Entry> findEntries(long userId, long guideId, int page) {
        checkUserHasGuideAccess(userId, guideId);
        List<EntryEntity> entities = repoEntry.findByGuide(
            guideId, pageSize, page * pageSize);
        return new Paged<>(
            page, pageSize, entities.size(),
            entities.stream().map(EntryMapper.INSTANCE::entityToDto).toList());
    }

    public @Valid Entry findEntry(long userId, long guideId, long entryId) {
        checkUserHasGuideAccess(userId, guideId);
        Optional<EntryEntity> foundEntity = repoEntry.findById(entryId);
        if (!foundEntity.isPresent()) {
            throw new NotFoundException("Entry not found!");
        }
        return EntryMapper.INSTANCE.entityToDto(foundEntity.get());
    }

    @Transactional
    public @Valid Entry createEntry(long userId, long guideId, @Valid EntryBase entryBase) {
        checkUserHasGuideAccess(userId, guideId);
        return EntryMapper.INSTANCE.entityToDto(
            repoEntry.save(EntryMapper.INSTANCE.dtoToEntity(
                EntryMapper.INSTANCE.baseDtoToFullDto(entryBase, guideId))));
    }

    @Transactional
    public @Valid Entry updateEntry(long userId, long guideId, long entryId, @Valid EntryBase entryBase) {
        checkUserHasGuideAccess(userId, guideId);
        Optional<EntryEntity> foundEntity = repoEntry.findById(entryId);
        if (!foundEntity.isPresent()) {
            throw new NotFoundException("Entry not found!");
        }
        return EntryMapper.INSTANCE.entityToDto(
            repoEntry.save(EntryMapper.INSTANCE.dtoToExistingEntity(
                foundEntity.get(), entryBase)));
    }

    @Transactional
    public void deleteEntry(long userId, long guideId, long entryId) {
        checkUserHasGuideAccess(userId, guideId);
        repoEntry.deleteById(entryId);
    }

    private void checkUserHasGuideAccess(long userId, long guideId) {
        Optional<GuideEntity> foundEntity = repoGuide.findById(guideId);
        if (!foundEntity.isPresent()) {
            throw new NotFoundException("Guide not found!");
        }
        GuideEntity entity = foundEntity.get();
        if (entity.getVisibility() == GuideVisibilityType.PRIVATE
                && !repoGuideOwner.existsByGuideIdAndUserId(guideId, userId)
                && !repoGuideMember.existsByGuideIdAndUserId(guideId, userId)) {
            throw new ForbiddenException("Entries from the related Guide are not accessible by this User!");
        }
    }

}
