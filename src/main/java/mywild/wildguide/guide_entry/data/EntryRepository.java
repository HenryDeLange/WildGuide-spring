package mywild.wildguide.guide_entry.data;

import java.util.List;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryRepository extends CrudRepository<EntryEntity, Long> {

    @Query("SELECT ge.* "
        + "FROM \"guide_entries\" ge " 
        + "WHERE ge.guide_id = :guideId "
        + "ORDER BY ge.name ASC "
        + "LIMIT :limit OFFSET :offset")
    List<EntryEntity> findByGuide(
        long guideId, int limit, int offset);

    @Modifying
    @Query("DELETE FROM \"guide_entries\" WHERE guide_id = :guideId")
    void deleteGuideEntries(long guideId);

}
