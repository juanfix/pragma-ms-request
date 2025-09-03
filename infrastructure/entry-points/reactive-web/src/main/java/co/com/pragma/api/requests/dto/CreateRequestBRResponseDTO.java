package co.com.pragma.api.requests.dto;

public record CreateRequestBRResponseDTO(
        String timestamp,
        String status,
        String error,
        String message
) {
}
