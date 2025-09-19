package co.com.pragma.usecase.requests.interfaces;

import co.com.pragma.usecase.requests.dto.SqsEmailMessageDTO;
import co.com.pragma.usecase.requests.dto.SqsReportMessageDTO;
import reactor.core.publisher.Mono;

public interface SqsUseCaseInterface {
    Mono<Void> publishStatusRequest(String queueName, SqsEmailMessageDTO sqsEmailMessageDTO);
    Mono<Void> publishReportRequest(String queueName, SqsReportMessageDTO sqsReportMessageDTO);
}
