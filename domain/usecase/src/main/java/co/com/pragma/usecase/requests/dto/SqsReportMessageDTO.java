package co.com.pragma.usecase.requests.dto;

import lombok.Builder;

@Builder
public record SqsReportMessageDTO(
        String id,
        Long countToAdd,
        Double totalAmountToAdd
) {
}
