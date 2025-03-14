package mywild.wildguide.domain.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import mywild.wildguide.domain.guide.data.GuideOwnerLinkRepository;
import mywild.wildguide.domain.guide.data.GuideRepository;
import mywild.wildguide.framework.error.ForbiddenException;
import mywild.wildguide.framework.error.NotFoundException;

@Validated
@Service
public abstract class DomainService {

    @Autowired
    private GuideRepository repoGuide;

    @Autowired
    private GuideOwnerLinkRepository repoGuideOwner;

    protected void checkUserHasGuideAccess(long userId, long guideId) {
        if (!repoGuide.existsById(guideId)) {
            throw new NotFoundException("guide.not-found");
        }
        if (repoGuide.countByVisibilityAndName(userId, null) <= 0) {
            throw new ForbiddenException("guide.not-accessible");
        }
    }

    protected void checkUserHasGuideOwnership(long userId, long guideId) {
        checkUserHasGuideAccess(userId, guideId);
        if (!repoGuideOwner.existsByGuideIdAndUserId(guideId, userId)) {
            throw new ForbiddenException("guide.not-accessible");
        }
    }
    
}
