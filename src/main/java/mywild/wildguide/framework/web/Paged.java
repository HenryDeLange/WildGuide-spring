package mywild.wildguide.framework.web;

import java.util.List;

public record Paged<T>(
    int pageNumber,
    int pageSize,
    long totalRecords,
    List<T> data
) { }
