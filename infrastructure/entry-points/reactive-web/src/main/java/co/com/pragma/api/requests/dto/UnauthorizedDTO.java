package co.com.pragma.api.requests.dto;

public record UnauthorizedDTO(
        String timestamp,
        String status,
        String error,
        String message
) {
}
