package co.com.pragma.api.requests.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateRequestsRequestDTO(
        @Schema(
                description = "Request amount",
                example = "2000000",
                minLength = 1
        )
        Long amount,

        @Schema(
                description = "Request term in months",
                example = "5",
                minLength = 1
        )
        Integer term,

        @Schema(
                description = "Email's user",
                example = "juan@mail.com",
                format = "email"
        )
        String email,

        @Schema(
                description = "DNI of the user",
                example = "123456",
                minLength = 6,
                maxLength = 20
        )
        String identityNumber,

        @Schema(
                description = "Loan type id assigned to the request.",
                example = "1",
                minimum = "0"
        )
        Long loanTypeId
) {
}
