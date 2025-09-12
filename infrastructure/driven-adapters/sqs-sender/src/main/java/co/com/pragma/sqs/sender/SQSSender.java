package co.com.pragma.sqs.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import co.com.pragma.usecase.requests.interfaces.SqsUseCaseInterface;
import co.com.pragma.usecase.requests.dto.SqsMessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements SqsUseCaseInterface {
    private final SQSSenderProperties sqsSenderProperties;
    private final SqsAsyncClient sqsAsyncClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(sqsAsyncClient.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(sqsSenderProperties.queueUrl())
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<Void> publishStatusRequest(SqsMessageDTO sqsMessageDTO) {
        return Mono.fromCallable(() -> toJson(sqsMessageDTO))
                .flatMap(this::send)
                .doOnSuccess(response -> log.info("Mensaje enviado a SQS: {}", response))
                .doOnError(err -> log.error("Error enviando mensaje a SQS: {}", err.getMessage()))
                .then();
    }

    private String toJson(SqsMessageDTO sqsMessageDTO) throws JsonProcessingException {
        return objectMapper.writeValueAsString(sqsMessageDTO);
    }
}
