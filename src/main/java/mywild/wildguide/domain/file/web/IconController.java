package mywild.wildguide.domain.file.web;

import java.net.MalformedURLException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mywild.wildguide.domain.file.data.IconCategory;
import mywild.wildguide.domain.file.logic.IconService;
import mywild.wildguide.framework.security.jwt.JwtUtils;
import mywild.wildguide.framework.web.BaseController;

@Tag(name = "Icons", description = "Upload and retrieve profile Icons.")
@RestController
public class IconController extends BaseController {

    @Autowired
    private IconService service;

    @Operation(summary = "Download the Icon associated with the specified category, if visible to the User (owner, member or public).")
    @GetMapping("/icons/{iconCategory}/{iconCategoryId}")
    public ResponseEntity<Resource> downloadIcon(
        @PathVariable IconCategory iconCategory,
        @PathVariable long iconCategoryId
    ) throws MalformedURLException {
        Resource fileResource = service.findIcon(
            iconCategory,
            iconCategoryId);
        // Add caching headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "max-age=3600, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "public");
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(fileResource);
    }

    @Operation(summary = "Create/Replace the Icon associated with the specified category.")
    @PostMapping(
        path = "/icons/{iconCategory}/{iconCategoryId}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> createIcon(
        JwtAuthenticationToken jwtToken,
        @PathVariable IconCategory iconCategory,
        @PathVariable long iconCategoryId,
        @RequestPart MultipartFile file
    ) {
        var url = service.createIcon(
            JwtUtils.getUserIdFromJwt(jwtToken),
            iconCategory,
            iconCategoryId,
            file);
        return Map.of("url", url);
    }

}
