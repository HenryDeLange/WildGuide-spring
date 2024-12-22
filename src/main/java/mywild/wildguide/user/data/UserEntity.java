package mywild.wildguide.user.data;

import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.NotBlank;
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
@Table("users")
public class UserEntity extends BaseEntity {

    @NotBlank
    @Size(min = 4, max = 64)
    private String username;

    @NotBlank
    @Size(min = 60, max = 60)
    private String password;

}
