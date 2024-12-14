package mywild.wildguide.guide.logic;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import mywild.wildguide.framework.error.BadRequestException;
import mywild.wildguide.framework.error.ForbiddenException;
import mywild.wildguide.framework.error.NotFoundException;
import mywild.wildguide.framework.web.Paged;
import mywild.wildguide.guide.data.GuideEntity;
import mywild.wildguide.guide.data.GuideMemberLink;
import mywild.wildguide.guide.data.GuideMemberLinkRepository;
import mywild.wildguide.guide.data.GuideOwnerLink;
import mywild.wildguide.guide.data.GuideOwnerLinkRepository;
import mywild.wildguide.guide.data.GuideRepository;
import mywild.wildguide.guide.data.GuideVisibilityType;
import mywild.wildguide.guide.web.Guide;
import mywild.wildguide.guide.web.GuideBase;

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

    public @Valid Paged<Guide> findGuides(long userId, int page) {
        List<GuideEntity> entities = repoGuide.findByVisibilityOrOwnerOrMember(
            GuideVisibilityType.PUBLIC, userId, userId, pageSize, page * pageSize);
        return new Paged<>(
            page, pageSize, entities.size(),
            entities.stream().map(GuideMapper.INSTANCE::entityToDto).toList());
    }

    public @Valid Guide findGuide(long userId, long guideId) {
        GuideEntity entity = findGuide(userId, guideId, false);
        boolean isOwner = repoGuideOwner.existsByGuideIdAndUserId(guideId, userId);
        boolean isMember = repoGuideMember.existsByGuideIdAndUserId(guideId, userId);
        if (entity.getVisibility() == GuideVisibilityType.PRIVATE && !isOwner && !isMember) {
            throw new ForbiddenException("Guide not accessible by this User!");
        }
        return GuideMapper.INSTANCE.entityToDto(entity);
    }

    @Transactional
    public @Valid Guide createGuide(long userId, @Valid GuideBase guideBase) {
        Guide guide = GuideMapper.INSTANCE.entityToDto(
            repoGuide.save(GuideMapper.INSTANCE.dtoToEntity(
                GuideMapper.INSTANCE.baseDtoToFullDto(guideBase))));
        repoGuideOwner.save(new GuideOwnerLink(0, guide.getId(), userId));
        return guide;
    }

    @Transactional
    public @Valid Guide updateGuide(long userId, long guideId,  @Valid GuideBase guideBase) {
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
        // TODO: Also delete all Guide Entry records
        repoGuide.delete(entity);
    }

    public List<Long> findGuideOwners(long guideId) {
        return repoGuideOwner.findAllByGuide(guideId);
    }

    @Transactional
    public boolean ownerJoinGuide(long userId, long guideId, long ownerId) {
        checkUserIsGuideOwner(userId, guideId);
        if (!repoGuideOwner.existsByGuideIdAndUserId(guideId, ownerId)) {
            repoGuideOwner.save(new GuideOwnerLink(0, guideId, ownerId));
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

    public List<Long> findGuideMembers(long guideId) {
        return repoGuideMember.findAllByGuide(guideId);
    }

    @Transactional
    public boolean memberJoinGuide(long userId, long guideId, long memberId) {
        checkUserIsGuideOwner(userId, guideId);
        if (!repoGuideMember.existsByGuideIdAndUserId(guideId, memberId)) {
            repoGuideMember.save(new GuideMemberLink(0, guideId, memberId));
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
