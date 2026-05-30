package pe.edu.reparaya.shared.util;

import java.util.List;

/**
 * Respuesta paginada genérica — usada en todos los endpoints de listado.
 */
public record PageResponse<T>(
        List<T> content,
        long    totalElements,
        int     totalPages,
        int     page,
        int     size
) {
    public static <T> PageResponse<T> of(List<T> content, long total, int page, int size) {
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) total / size);
        return new PageResponse<>(content, total, totalPages, page, size);
    }

    public static <T> PageResponse<T> from(org.springframework.data.domain.Page<T> springPage) {
        return new PageResponse<>(
                springPage.getContent(),
                springPage.getTotalElements(),
                springPage.getTotalPages(),
                springPage.getNumber(),
                springPage.getSize()
        );
    }
}
