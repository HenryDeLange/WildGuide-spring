package mywild.wildguide.guide.data;

import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuideRepository extends CrudRepository<GuideEntity, Long> {

    @Query("SELECT g.* "
        + "FROM \"guides\" g " 
        + "LEFT JOIN \"guide_owners\" go ON g.id = go.guide_id "
        + "LEFT JOIN \"guide_members\" gm ON g.id = gm.guide_id " 
        + "WHERE g.visibility = :visibility "
        + "OR go.user_id = :ownerId " 
        + "OR gm.user_id = :memberId "
        + "ORDER BY g.name ASC "
        + "LIMIT :limit OFFSET :offset")
    List<GuideEntity> findByVisibilityOrOwnerOrMember(
        GuideVisibilityType visibility, long ownerId, long memberId, int limit, int offset);

}
