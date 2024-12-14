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

    // TODO: Add Git info here

    @Value("${mywild.app.version}")
    private String version;

    @Operation(summary = "Get server version.")
    @GetMapping("")
    public Version getVersion() {
        return Version.builder().appVersion(version).build();
    }

}
