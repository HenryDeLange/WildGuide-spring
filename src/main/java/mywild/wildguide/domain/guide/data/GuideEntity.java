package mywild.wildguide.domain.guide.data;

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
@Table("guides")
public class GuideEntity extends BaseEntity {

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
