package mywild.wildguide.user.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserLogin {

    @NotBlank
    @Size(min = 4, max = 64)
    private String username;

    @NotBlank
    @Size(min = 8, max = 256)
    private String password;

}
