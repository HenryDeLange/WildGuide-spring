package mywild.wildguide.domain.file.logic;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import mywild.wildguide.domain.file.data.FileCategory;
import mywild.wildguide.domain.file.data.IconCategory;
import mywild.wildguide.domain.utils.DomainService;
import mywild.wildguide.framework.error.ApplicationException;
import mywild.wildguide.framework.error.NotFoundException;

@Slf4j
@Validated
@Service
public class IconService extends DomainService {

    protected static final String ICON_FILE_ID = "icon";
    private static final String ICON_FILE_NAME = "avatar";

    @Autowired
    private FileService fileService;

    @Value("${mywild.wildguide.file-upload-path}")
    private String fileUploadFolder;

    @Value("${mywild.api-path}")
    private String apiPath;

    private Path fileUploadPath;

    @PostConstruct
    public void init() throws IOException {
        fileUploadPath = Paths.get(fileUploadFolder).normalize().toAbsolutePath();
        log.debug("Initialising file upload path: {}", fileUploadPath);
        if (!Files.exists(fileUploadPath)) {
            Files.createDirectories(fileUploadPath);
        }
    }

    public Resource findIcon(IconCategory iconCategory, long iconCategoryId) throws MalformedURLException {
        var fileCategory = getFileCategory(iconCategory);
        Path basePath = fileService.getBasePath(fileCategory, iconCategoryId)
            .resolve(ICON_FILE_ID);
        try (Stream<Path> stream = Files.walk(basePath)) {
            var files = stream
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
            if (files.size() != 1) {
                throw new NotFoundException("file.not-found");
            }
            var iconFile = files.get(0);
            Path filePath = fileService.getFilePath(
                fileCategory,
                iconCategoryId,
                ICON_FILE_ID,
                iconFile.getFileName().toString());
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new NotFoundException("file.not-found");
            }
            return resource;
        }
        catch (NoSuchFileException ex) {
            throw new NotFoundException("file.not-found");
        }
        catch (IOException ex) {
            throw new ApplicationException("file.error");
        }
    }

    public String createIcon(long userId, IconCategory iconCategory, long iconCategoryId, @NotNull MultipartFile file) {
        var fileCategory = getFileCategory(iconCategory);
        fileService.checkVisibility(userId, fileCategory, iconCategoryId);
        Path filePath = fileService.getFilePath(
            fileCategory, 
            iconCategoryId, 
            ICON_FILE_ID, 
            ICON_FILE_NAME);
        try {
            if (!Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException ex) {
            throw new ApplicationException("file.error");
        }
        return getFileUrl(userId, iconCategory, iconCategoryId);
    }

    private String getFileUrl(long userId, IconCategory iconCategory, long iconCategoryId) {
        return apiPath + "/icons/" + iconCategory + "/" + iconCategoryId;
    }

    private FileCategory getFileCategory(IconCategory iconCategory) {
        switch (iconCategory) {
            case USER:
                return FileCategory.USER;
            case GUIDE:
                return FileCategory.GUIDE;
            default:
                return null;
        }
    }

}
