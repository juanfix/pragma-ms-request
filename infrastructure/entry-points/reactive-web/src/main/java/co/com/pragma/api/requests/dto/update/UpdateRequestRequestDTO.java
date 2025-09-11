package co.com.pragma.api.requests.dto.update;

import jakarta.validation.constraints.NotBlank;

public record UpdateRequestRequestDTO(
        @NotBlank(message = "New status is required.")
        Long newStatusId
) {
}
