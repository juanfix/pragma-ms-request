package co.com.pragma.sqs.sender;

import co.com.pragma.usecase.requests.dto.SqsReportMessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import co.com.pragma.usecase.requests.interfaces.SqsUseCaseInterface;
import co.com.pragma.usecase.requests.dto.SqsEmailMessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.net.URI;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements SqsUseCaseInterface {
    private final Map<String, SqsAsyncClient> sqsAsyncClient;
    private final SQSSenderProperties sqsSenderProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<String> send(String queueName, String message) {
        var queueProps = sqsSenderProperties.queues().get(queueName);

        if (queueProps == null) {
            return Mono.error(new IllegalArgumentException("Queue not found: " + queueName));
        }

        var sqsAsyncClient = SqsAsyncClient.builder()
                .region(Region.of(queueProps.region()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .endpointOverride(queueProps.endpoint() != null ? URI.create(queueProps.endpoint()) : null)
                .build();

        return Mono.fromCallable(() -> buildRequest(message, queueProps.queueUrl()))
                .flatMap(request -> Mono.fromFuture(sqsAsyncClient.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message, String queueUrl) {
        return SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<Void> publishStatusRequest(String queueName, SqsEmailMessageDTO sqsEmailMessageDTO) {
        return Mono.fromCallable(() -> toJson(sqsEmailMessageDTO))
                .flatMap(json -> {
                    return send(queueName, json);
                })
                .doOnSuccess(response -> log.info("Mensaje enviado a SQS email: {}", response))
                .doOnError(err -> log.error("Error enviando mensaje a SQS email: {}", err.getMessage()))
                .then();
    }

    @Override
    public Mono<Void> publishReportRequest(String queueName, SqsReportMessageDTO sqsReportMessageDTO) {
        return Mono.fromCallable(() -> toJson(sqsReportMessageDTO))
                .flatMap(json -> {
                    return send(queueName, json);
                })
                .doOnSuccess(response -> log.info("Mensaje enviado a SQS reporte: {}", response))
                .doOnError(err -> log.error("Error enviando mensaje a SQS reporte: {}", err.getMessage()))
                .then();
    }

    private String toJson(Record sqsMessageDTO) throws JsonProcessingException {
        return objectMapper.writeValueAsString(sqsMessageDTO);
    }
}
