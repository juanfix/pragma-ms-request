package co.com.pragma.model.requests.dto;

public record PagedSummary<T>(
        java.util.List<T> request,
        int page,
        int size,
        Long total
) {

}
