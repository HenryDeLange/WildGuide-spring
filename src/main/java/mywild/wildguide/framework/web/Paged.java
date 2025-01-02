package mywild.wildguide.framework.web;

import java.util.List;
import jakarta.validation.constraints.NotNull;

public record Paged<T>(
    @NotNull
    int pageNumber,
    @NotNull
    int pageSize,
    @NotNull
    int totalRecords,
    @NotNull
    List<T> data
) { }
