package co.com.pragma.api.requests.dto.create;

public record CreateRequestsFailResponseDTO(
        String timestamp,
        String path,
        String status,
        String error,
        String requestId,
        String message,
        String trace
) {
}
