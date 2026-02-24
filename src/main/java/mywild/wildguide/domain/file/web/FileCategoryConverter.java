package mywild.wildguide.domain.file.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import mywild.wildguide.domain.file.data.FileCategory;

// Use a converter to help the endpoint handle lowercase enums
@Component
public class FileCategoryConverter implements Converter<String, FileCategory> {

    @Override
    public FileCategory convert(@NonNull String value) {
        return FileCategory.valueOf(value.toUpperCase());
    }

}
