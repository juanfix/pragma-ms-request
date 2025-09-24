package co.com.pragma.api.requests.dto.create;

public record CreateRequestsReponseDTO(
        Long id,
        Long amount,
        Long term,
        String identityNumber,
        String email,
        Long statusId,
        Long loanTYpeId
) {
}
