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
public class Tokens {

    @NotBlank
    @Size(min = 4)
    private String username;

    @NotBlank
    private String accessToken;

    @NotBlank
    private String refreshToken;

}
