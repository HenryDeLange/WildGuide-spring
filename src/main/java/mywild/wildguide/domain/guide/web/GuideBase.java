package mywild.wildguide.domain.guide.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import mywild.wildguide.domain.guide.data.GuideVisibilityType;

@ToString(callSuper = true)
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GuideBase {

    @NotBlank
    @Size(min = 4, max = 128)
    private String name;

    @Size(max = 256)
    private String summary;

    private String description;

    @NotNull
    private GuideVisibilityType visibility;

    private String inaturalistCriteria;

}
