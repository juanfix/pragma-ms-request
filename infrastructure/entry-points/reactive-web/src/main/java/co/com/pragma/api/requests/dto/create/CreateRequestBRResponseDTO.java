package co.com.pragma.api.requests.dto.create;

public record CreateRequestBRResponseDTO(
        String timestamp,
        String status,
        String error,
        String message
) {
}
