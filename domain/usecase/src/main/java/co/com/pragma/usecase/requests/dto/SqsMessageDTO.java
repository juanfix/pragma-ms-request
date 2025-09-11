package co.com.pragma.usecase.requests.dto;

import lombok.Builder;

@Builder
public record SqsMessageDTO(String to, String subject, String body) {
}
