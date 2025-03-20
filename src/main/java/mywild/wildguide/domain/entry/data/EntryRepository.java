package mywild.wildguide.domain.entry.data;

import java.util.List;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryRepository extends CrudRepository<EntryEntity, Long> {

    static final String FIND_ENTRIES =
        "FROM \"guide_entries\" ge "
        + "WHERE ("
        + " ge.guide_id = :guideId "
        + ") AND ("
        + " :name IS NULL"
        + " OR LOWER(ge.name) LIKE CONCAT('%', LOWER(:name), '%')"
        + " OR LOWER(ge.scientific_name) LIKE CONCAT('%', LOWER(:name), '%')"
        + ")";

    @Query("SELECT ge.* " + FIND_ENTRIES + " ORDER BY LOWER(ge.name) ASC LIMIT :limit OFFSET :offset")
    List<EntryEntity> findByGuide(
        long guideId, String name, int limit, int offset);

    @Query("SELECT COUNT(ge.id) " + FIND_ENTRIES)
    int countByGuide(
        long guideId, String name);

    @Modifying
    @Query("DELETE FROM \"guide_entries\" WHERE guide_id = :guideId")
    void deleteGuideEntries(
        long guideId);

    @Query("SELECT ge.id AS entry_id, ge.inaturalist_taxon "
    + "FROM \"guide_entries\" ge "
    + "WHERE ge.guide_id = :guideId "
    + "AND ge.inaturalist_taxon IS NOT NULL")
    List<EntryScientificName> findEntriesScientificNames(
        long guideId);

}
