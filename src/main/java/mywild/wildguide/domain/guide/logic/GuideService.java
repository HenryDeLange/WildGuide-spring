package mywild.wildguide.domain.guide.logic;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import mywild.wildguide.domain.entry.data.EntryRepository;
import mywild.wildguide.domain.guide.data.GuideEntity;
import mywild.wildguide.domain.guide.data.GuideEntityExtended;
import mywild.wildguide.domain.guide.data.GuideLinkedUser;
import mywild.wildguide.domain.guide.data.GuideMemberLink;
import mywild.wildguide.domain.guide.data.GuideMemberLinkRepository;
import mywild.wildguide.domain.guide.data.GuideOwnerLink;
import mywild.wildguide.domain.guide.data.GuideOwnerLinkRepository;
import mywild.wildguide.domain.guide.data.GuideRepository;
import mywild.wildguide.domain.guide.data.GuideStarLink;
import mywild.wildguide.domain.guide.data.GuideStarLinkRepository;
import mywild.wildguide.domain.guide.web.Guide;
import mywild.wildguide.domain.guide.web.GuideBase;
import mywild.wildguide.domain.utils.DomainService;
import mywild.wildguide.framework.error.BadRequestException;
import mywild.wildguide.framework.web.Paged;

@Validated
@Service
public class GuideService extends DomainService {

    @Value("${mywild.wildguide.page-size}")
    private int pageSize;

    @Autowired
    private GuideRepository repoGuide;

    @Autowired
    private GuideOwnerLinkRepository repoGuideOwner;

    @Autowired
    private GuideMemberLinkRepository repoGuideMember;

    @Autowired
    private GuideStarLinkRepository repoGuideStar;

    @Autowired
    private EntryRepository repoEntry;

    public @Valid Paged<Guide> findGuides(long userId, int page, String name) {
        int totalCount = repoGuide.countByVisibilityAndName(userId, name);
        List<GuideEntityExtended> entities = repoGuide.findByVisibilityAndName(userId, name, pageSize, page * pageSize);
        return new Paged<>(
            page, pageSize, totalCount,
            entities.stream().map(GuideMapper.INSTANCE::entityToDto).toList());
    }

    public @Valid Guide findGuide(long userId, long guideId) {
        GuideEntityExtended entity = getAccessibleGuide(userId, guideId);
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
        GuideEntity entity = getOwnedGuide(userId, guideId);
        return GuideMapper.INSTANCE.entityToDto(
            repoGuide.save(GuideMapper.INSTANCE.dtoToExistingEntity(
                entity, guideBase)));
    }

    @Transactional
    public void deleteGuide(long userId, long guideId) {
        checkUserHasGuideOwnership(userId, guideId);
        GuideEntity entity = getOwnedGuide(userId, guideId);
        repoGuideOwner.deleteGuideOwners(guideId);
        repoGuideMember.deleteGuideMembers(guideId);
        repoEntry.deleteGuideEntries(guideId);
        repoGuideStar.deleteGuideStars(guideId);
        repoGuide.delete(entity);
    }

    public List<GuideLinkedUser> findGuideOwners(long guideId) {
        return repoGuideOwner.findAllByGuide(guideId);
    }

    @Transactional
    public boolean ownerJoinGuide(long userId, long guideId, long ownerId) {
        checkUserHasGuideOwnership(userId, guideId);
        if (!repoGuideOwner.existsByGuideIdAndUserId(guideId, ownerId)) {
            repoGuideOwner.save(new GuideOwnerLink(guideId, ownerId));
            return true;
        }
        return false;
    }

    @Transactional
    public boolean ownerLeaveGuide(long userId, long guideId, long ownerId) {
        checkUserHasGuideOwnership(userId, guideId);
        if (repoGuideOwner.existsByGuideIdAndUserId(guideId, ownerId)) {
            if (repoGuideOwner.findAllByGuide(guideId).size() == 1) {
                throw new BadRequestException("guide.one-owner");
            }
            repoGuideOwner.deleteByGuideAndUser(guideId, ownerId);
            return true;
        }
        return false;
    }

    public List<Guide> findStarredGuides(long userId) {
        List<GuideEntityExtended> entities = repoGuideStar.findStarredGuidesByUser(userId);
        return entities.stream().map(GuideMapper.INSTANCE::entityToDto).toList();
    }

    @Transactional
    public boolean createGuideStar(long userId, long guideId) {
        repoGuideStar.save(new GuideStarLink(userId, guideId));
        return true;
    }

    @Transactional
    public boolean deleteGuideStar(long userId, long guideId) {
        repoGuideStar.deleteByUserAndGuide(userId, guideId);
        return true;
    }

    public List<GuideLinkedUser> findGuideMembers(long guideId) {
        return repoGuideMember.findAllByGuide(guideId);
    }

    @Transactional
    public boolean memberJoinGuide(long userId, long guideId, long memberId) {
        checkUserHasGuideOwnership(userId, guideId);
        if (!repoGuideMember.existsByGuideIdAndUserId(guideId, memberId)) {
            repoGuideMember.save(new GuideMemberLink(guideId, memberId));
            return true;
        }
        return false;
    }

    @Transactional
    public boolean memberLeaveGuide(long userId, long guideId, long memberId) {
        checkUserHasGuideOwnership(userId, guideId);
        if (repoGuideMember.existsByGuideIdAndUserId(guideId, memberId)) {
            repoGuideMember.deleteByGuideAndUser(guideId, memberId);
            return true;
        }
        return false;
    }

    private GuideEntityExtended getAccessibleGuide(long userId, long guideId) {
        checkUserHasGuideAccess(userId, guideId);
        return repoGuide.findExtendedById(userId, guideId);
    }
    
    private GuideEntity getOwnedGuide(long userId, long guideId) {
        checkUserHasGuideOwnership(userId, guideId);
        return repoGuide.findById(guideId).get();
    }

}
