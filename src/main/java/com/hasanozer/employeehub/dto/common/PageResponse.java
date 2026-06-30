package com.hasanozer.employeehub.dto.common;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        int numberOfElements,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}
