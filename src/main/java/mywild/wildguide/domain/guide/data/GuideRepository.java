package mywild.wildguide.domain.guide.data;

import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuideRepository extends CrudRepository<GuideEntity, Long> {

    static final String SELECT_EXTENDED_GUIDE =
        "SELECT g.*, "
        + "(CASE WHEN"
        + " (SELECT gs.id AS starred_by_user FROM \"guide_stars\" gs WHERE gs.user_id = :userId AND gs.guide_id = g.id)"
        + " IS NOT NULL THEN TRUE ELSE FALSE END"
        + ") AS starred_by_user ";

    static final String FIND_GUIDES =
        "FROM \"guides\" g "
        + "LEFT JOIN \"guide_owners\" go ON g.id = go.guide_id AND go.user_id = :userId "
        + "LEFT JOIN \"guide_members\" gm ON g.id = gm.guide_id AND gm.user_id = :userId "
        + "WHERE ("
        + " g.visibility = 'PUBLIC'"
        + " OR go.user_id = :userId"
        + " OR gm.user_id = :userId"
        + " ) "
        + "AND ("
        + " :name IS NULL"
        + " OR LOWER(g.name) LIKE CONCAT('%', LOWER(:name), '%')"
        + " )";

    @Query(SELECT_EXTENDED_GUIDE + FIND_GUIDES + " ORDER BY LOWER(g.name) ASC LIMIT :limit OFFSET :offset")
    List<GuideEntityExtended> findByVisibilityAndName(long userId, String name, 
        int limit, int offset);
    
    @Query("SELECT COUNT(g.id) " + FIND_GUIDES)
    int countByVisibilityAndName(long userId, String name);
    
    @Query(SELECT_EXTENDED_GUIDE
        + "FROM \"guides\" g "
        + "WHERE g.id = :guideId")
    GuideEntityExtended findExtendedById(long userId, long guideId);

}
