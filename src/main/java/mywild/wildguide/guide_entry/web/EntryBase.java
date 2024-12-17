package mywild.wildguide.guide_entry.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@ToString(callSuper = true)
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EntryBase {

    @NotBlank
    @Size(min = 1, max = 128)
    private String name;

    private String summary;

    private String description;

    private Long inaturalistTaxon;

}
