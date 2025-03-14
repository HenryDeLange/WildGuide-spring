package mywild.wildguide.domain.guide.data;

import java.util.List;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface GuideStarLinkRepository extends CrudRepository<GuideStarLink, Long> {

    boolean existsByUserIdAndGuideId(long userId, long guideId);

    @Query("SELECT g.*, TRUE AS starred_by_user "
        + "FROM \"guide_stars\" "
        + "LEFT JOIN \"guides\" g ON guide_id = g.id "
        + "WHERE user_id = :userId "
        + "ORDER BY LOWER(g.name) ASC")
    List<GuideEntityExtended> findStarredGuidesByUser(long userId);

    @Modifying
    @Query("DELETE FROM \"guide_stars\" WHERE user_id = :userId AND guide_id = :guideId")
    void deleteByUserAndGuide(long userId, long guideId);

    @Modifying
    @Query("DELETE FROM \"guide_stars\" WHERE guide_id = :guideId")
    void deleteGuideStars(long guideId);

}
