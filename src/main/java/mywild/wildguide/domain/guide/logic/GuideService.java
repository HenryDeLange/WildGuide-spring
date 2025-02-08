package mywild.wildguide.domain.guide.logic;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import mywild.wildguide.domain.entry.data.EntryRepository;
import mywild.wildguide.framework.error.BadRequestException;
import mywild.wildguide.framework.error.ForbiddenException;
import mywild.wildguide.framework.error.NotFoundException;
import mywild.wildguide.framework.web.Paged;
import mywild.wildguide.domain.guide.data.GuideEntity;
import mywild.wildguide.domain.guide.data.GuideLinkedUser;
import mywild.wildguide.domain.guide.data.GuideMemberLink;
import mywild.wildguide.domain.guide.data.GuideMemberLinkRepository;
import mywild.wildguide.domain.guide.data.GuideOwnerLink;
import mywild.wildguide.domain.guide.data.GuideOwnerLinkRepository;
import mywild.wildguide.domain.guide.data.GuideRepository;
import mywild.wildguide.domain.guide.data.GuideVisibilityType;
import mywild.wildguide.domain.guide.web.Guide;
import mywild.wildguide.domain.guide.web.GuideBase;

@Validated
@Service
public class GuideService {

    @Value("${mywild.wildguide.page-size}")
    private int pageSize;

    @Autowired
    private GuideRepository repoGuide;

    @Autowired
    private GuideOwnerLinkRepository repoGuideOwner;

    @Autowired
    private GuideMemberLinkRepository repoGuideMember;

    @Autowired
    private EntryRepository repoEntry;

    public @Valid Paged<Guide> findGuides(long userId, int page, String name) {
        int totalCount = repoGuide.countByVisibilityOrOwnerOrMember(
            GuideVisibilityType.PUBLIC, userId, userId, name);
        List<GuideEntity> entities = repoGuide.findByVisibilityOrOwnerOrMember(
            GuideVisibilityType.PUBLIC, userId, userId, name, pageSize, page * pageSize);
        return new Paged<>(
            page, pageSize, totalCount,
            entities.stream().map(GuideMapper.INSTANCE::entityToDto).toList());
    }

    public @Valid Guide findGuide(long userId, long guideId) {
        GuideEntity entity = findGuide(userId, guideId, false);
        if (entity.getVisibility() == GuideVisibilityType.PRIVATE
                && !repoGuideOwner.existsByGuideIdAndUserId(guideId, userId)
                && !repoGuideMember.existsByGuideIdAndUserId(guideId, userId)) {
            throw new ForbiddenException("Guide not accessible by this User!");
        }
        return GuideMapper.INSTANCE.entityToDto(entity);
    }

    @Transactional
    public @Valid Guide createGuide(long userId, @Valid GuideBase guideBase) {
        Guide entity = GuideMapper.INSTANCE.entityToDto(
            repoGuide.save(GuideMapper.INSTANCE.dtoToEntity(
                GuideMapper.INSTANCE.baseDtoToFullDto(guideBase))));
        repoGuideOwner.save(new GuideOwnerLink(entity.getId(), userId));
        return entity;
    }

    @Transactional
    public @Valid Guide updateGuide(long userId, long guideId, @Valid GuideBase guideBase) {
        GuideEntity entity = findGuide(userId, guideId, true);
        return GuideMapper.INSTANCE.entityToDto(
            repoGuide.save(GuideMapper.INSTANCE.dtoToExistingEntity(
                entity, guideBase)));
    }

    @Transactional
    public void deleteGuide(long userId, long guideId) {
        GuideEntity entity = findGuide(userId, guideId, true);
        repoGuideOwner.deleteGuideOwners(guideId);
        repoGuideMember.deleteGuideMembers(guideId);
        repoEntry.deleteGuideEntries(guideId);
        repoGuide.delete(entity);
    }

    public List<GuideLinkedUser> findGuideOwners(long guideId) {
        return repoGuideOwner.findAllByGuide(guideId);
    }

    @Transactional
    public boolean ownerJoinGuide(long userId, long guideId, long ownerId) {
        checkUserIsGuideOwner(userId, guideId);
        if (!repoGuideOwner.existsByGuideIdAndUserId(guideId, ownerId)) {
            repoGuideOwner.save(new GuideOwnerLink(guideId, ownerId));
            return true;
        }
        return false;
    }

    @Transactional
    public boolean ownerLeaveGuide(long userId, long guideId, long ownerId) {
        checkUserIsGuideOwner(userId, guideId);
        if (repoGuideOwner.existsByGuideIdAndUserId(guideId, ownerId)) {
            if (repoGuideOwner.findAllByGuide(guideId).size() == 1) {
                throw new BadRequestException("The Guide must have at least one Owner!");
            }
            repoGuideOwner.deleteByGuideIdAndUserId(guideId, ownerId);
            return true;
        }
        return false;
    }

    public List<GuideLinkedUser> findGuideMembers(long guideId) {
        return repoGuideMember.findAllByGuide(guideId);
    }

    @Transactional
    public boolean memberJoinGuide(long userId, long guideId, long memberId) {
        checkUserIsGuideOwner(userId, guideId);
        if (!repoGuideMember.existsByGuideIdAndUserId(guideId, memberId)) {
            repoGuideMember.save(new GuideMemberLink(guideId, memberId));
            return true;
        }
        return false;
    }

    @Transactional
    public boolean memberLeaveGuide(long userId, long guideId, long memberId) {
        checkUserIsGuideOwner(userId, guideId);
        if (repoGuideMember.existsByGuideIdAndUserId(guideId, memberId)) {
            repoGuideMember.deleteByGuideIdAndUserId(guideId, memberId);
            return true;
        }
        return false;
    }

    private GuideEntity findGuide(long userId, long guideId, boolean checkOwner) {
        Optional<GuideEntity> foundEntity = repoGuide.findById(guideId);
        if (!foundEntity.isPresent()) {
            throw new NotFoundException("Guide not found!");
        }
        GuideEntity entity = foundEntity.get();
        if (checkOwner) {
            checkUserIsGuideOwner(userId, guideId);
        }
        return entity;
    }

    private void checkUserIsGuideOwner(long userId, long guideId) {
        boolean isUserAnOwner = repoGuideOwner.existsByGuideIdAndUserId(guideId, userId);
        if (!isUserAnOwner) {
            throw new ForbiddenException("Not a Guide Owner!");
        }
    }

}
