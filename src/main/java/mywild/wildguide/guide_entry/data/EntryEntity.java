package mywild.wildguide.guide_entry.data;

import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import mywild.wildguide.framework.data.BaseEntity;

@ToString(callSuper = true)
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("guide_entries")
public class EntryEntity extends BaseEntity {

    private long guideId;

    @NotBlank
    @Size(min = 1, max = 128)
    private String name;

    @NotBlank
    @Size(min = 3, max = 256)
    private String scientificName;

    @NotNull
    private ScientificRank scientificRank;

    @Size(max = 256)
    private String summary;

    private String description;

    private Long inaturalistTaxon;

}
