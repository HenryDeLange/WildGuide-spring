package mywild.wildguide.version.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mywild.wildguide.framework.web.BaseController;

@Tag(name = "WildGuide Version", description = "Version information about the WildGuide server.")
@RestController
public class VersionController extends BaseController {

    @Value("${mywild.app.version}")
    private String appVersion;

    @Value("${git.branch}")
    private String branch;

    @Value("${git.commit.id.abbrev}")
    private String commitId;

    @Value("${git.commit.time}")
    private String commitTime;

    @Value("${git.build.time}")
    private String buildTime;

    @Operation(summary = "Get server version and Git information.")
    @GetMapping("/version")
    public Version getVersion() {
        return Version.builder()
            .appVersion(appVersion)
            .branch(branch)
            .commitId(commitId)
            .commitTime(commitTime)
            .buildTime(buildTime)
            .build();
    }

}
