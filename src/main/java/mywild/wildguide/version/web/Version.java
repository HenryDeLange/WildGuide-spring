package mywild.wildguide.version.web;

import org.springframework.beans.factory.annotation.Value;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Version {

    @NotBlank
    private String appVersion;

    @NotBlank
    private String branch;

    @NotBlank
    private String commitId;

    @NotBlank
    private String commitTime;

    @NotBlank
    private String buildTime;

}
