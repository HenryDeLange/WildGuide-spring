package mywild.wildguide.guide.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import mywild.wildguide.guide.data.GuideVisibilityType;

@ToString(callSuper = true)
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GuideBase {

    @NotBlank
    @Size(min = 4, max = 64)
    private String name;

    private String description;

    @NotNull
    private GuideVisibilityType visibility;

}
