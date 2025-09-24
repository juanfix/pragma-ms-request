package co.com.pragma.sqs.listener.dto;

public record UpdateRequestsRequestDTO(
        Long requestId,
        Long newStatusId
) {
}
