package mywild.wildguide.guide.data;

import java.util.List;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface GuideOwnerLinkRepository extends CrudRepository<GuideOwnerLink, Long> {

    boolean existsByGuideIdAndUserId(long guideId, long userId);

    @Query("SELECT user_id, u.username "
        + "FROM \"guide_owners\" "
        + "LEFT JOIN \"users\" u ON user_id = u.id "
        + "WHERE guide_id = :guideId "
        + "ORDER BY u.username ASC")
    List<GuideLinkedUser> findAllByGuide(long guideId);

    @Modifying
    @Query("DELETE FROM \"guide_owners\" WHERE guide_id = :guideId AND user_id = :userId")
    void deleteByGuideIdAndUserId(long guideId, long userId);

    @Modifying
    @Query("DELETE FROM \"guide_owners\" WHERE guide_id = :guideId")
    void deleteGuideOwners(long guideId);

}
