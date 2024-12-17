package mywild.wildguide.version.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "WildGuide Version", description = "Version information about the WildGuide server.")
@RestController
@RequestMapping("version")
public class VersionController {

    @Value("${mywild.app.version}")
    private String appVersion;

    @Value("${git.commit.id.abbrev}")
    private String commitId;

    @Value("${git.branch}")
    private String branch;

    @Value("${git.build.time}")
    private String buildTime;

    @Operation(summary = "Get server version.")
    @GetMapping("")
    public Version getVersion() {
        return Version.builder()
            .appVersion(appVersion)
            .commitId(commitId)
            .branch(branch)
            .buildTime(buildTime)
            .build();
    }

}
