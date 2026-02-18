package mywild.wildguide.domain.file.web;

import java.net.MalformedURLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mywild.wildguide.domain.file.data.FileCategory;
import mywild.wildguide.domain.file.logic.FileService;
import mywild.wildguide.framework.security.jwt.JwtUtils;
import mywild.wildguide.framework.web.BaseController;

@Tag(name = "Files", description = "Upload and retrieve Files.")
@RestController
public class FileController extends BaseController {

    @Autowired
    private FileService service;
    
    @Operation(summary = "Find all Files associated with the specified resource, that are visible to User (owner, member or public).")
    @GetMapping("/users/{userId}/files/{fileCategory}/{fileCategoryId}")
    public List<String> findFiles(
        @PathVariable FileCategory fileCategory,
        @PathVariable String fileCategoryId,
        @PathVariable String userId
    ) {
        return service.findFiles(
            Long.parseLong(userId),
            fileCategory,
            Long.parseLong(fileCategoryId));
    }

    @Operation(summary = "Download the specified File, if visible to the User (owner, member or public).")
    @GetMapping("/users/{userId}/files/{fileCategory}/{fileCategoryId}/{fileId}/{filename}")
    public ResponseEntity<Resource> downloadFile(
        JwtAuthenticationToken jwtToken,
        @PathVariable FileCategory fileCategory,
        @PathVariable String fileCategoryId,
        @PathVariable String fileId,
        @PathVariable String filename,
        @PathVariable String userId
    ) throws MalformedURLException {
        Resource fileResource = service.findFile(
            Long.parseLong(userId),
            fileCategory,
            Long.parseLong(fileCategoryId),
            fileId,
            filename);
        // Add caching headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "max-age=3600, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "public");
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(fileResource);
    }

    @Operation(summary = "Create a new File.")
    @PostMapping(path = "/files/{fileCategory}/{fileCategoryId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createFile(
        JwtAuthenticationToken jwtToken,
        @PathVariable FileCategory fileCategory,
        @PathVariable String fileCategoryId,
        @RequestPart MultipartFile file
    ) {
        return service.createFile(
            JwtUtils.getUserIdFromJwt(jwtToken),
            fileCategory,
            Long.parseLong(fileCategoryId),
            file);
    }

    @Operation(summary = "Delete a specific File.")
    @DeleteMapping("/files/{fileCategory}/{fileCategoryId}/{fileId}/{fileName}")
    public void deleteFile(
        JwtAuthenticationToken jwtToken,
        @PathVariable FileCategory fileCategory,
        @PathVariable String fileCategoryId,
        @PathVariable String fileId,
        @PathVariable String fileName
    ) {
        service.deleteFile(
            JwtUtils.getUserIdFromJwt(jwtToken),
            fileCategory,
            Long.parseLong(fileCategoryId),
            fileId,
            fileName);
    }

}
