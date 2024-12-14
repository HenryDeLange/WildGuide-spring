package mywild.wildguide.guide.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
    @Size(min = 4, max = 64)
    private String name;

    private String description;

    @NotNull
    private GuideVisibilityType visibility;

}
