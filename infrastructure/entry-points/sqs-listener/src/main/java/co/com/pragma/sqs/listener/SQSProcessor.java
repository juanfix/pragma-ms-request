package co.com.pragma.sqs.listener;

import co.com.pragma.sqs.listener.dto.UpdateRequestsRequestDTO;
import co.com.pragma.usecase.requests.UpdateRequestsUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {
    private static final Logger log = LoggerFactory.getLogger(SQSProcessor.class);
    private final UpdateRequestsUseCase updateRequestsUseCase;

    @Override
    public Mono<Void> apply(Message message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info("Se recibio un mensaje nuevo a la cola de SQS para actualizar la solicitud: {}", message.body());
            UpdateRequestsRequestDTO body = mapper.readValue(message.body().toString(), UpdateRequestsRequestDTO.class);

            return updateRequestsUseCase.execute(body.requestId(), body.newStatusId()).then();
        } catch (Exception e) {
            log.error("Error procesando mensaje: {}", e.getMessage(), e);
            return Mono.error(e);
        }
    }
}
