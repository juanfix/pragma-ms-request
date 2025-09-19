package co.com.pragma.usecase.requests.dto;

import lombok.Builder;

@Builder
public record SqsEmailMessageDTO(String to, String subject, String body) {
}
