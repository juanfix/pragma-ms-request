package co.com.pragma.usecase.requests;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.requests.Requests;
import co.com.pragma.model.requests.gateways.RequestsRepository;
import co.com.pragma.model.status.Status;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.usecase.requests.dto.SqsMessageDTO;
import co.com.pragma.usecase.requests.interfaces.SqsUseCaseInterface;
import co.com.pragma.usecase.requests.validations.error.RequestsValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class UpdateRequestsUseCase implements UpdateRequestsUseCaseInterface {

    private static final Logger logger = Logger.getLogger(UpdateRequestsUseCase.class.getName());

    private final LoanTypeRepository loanTypeRepository;
    private final StatusRepository statusRepository;
    private final RequestsRepository requestsRepository;
    private final SqsUseCaseInterface sqsUseCaseInterface;

    @Override
    public Mono<Requests> execute(Long requestsId, Long newStatusId) {
        logger.info(String.format("Iniciando proceso de actualización de solicitud [%d] al estado [%s]", requestsId, newStatusId));

        return requestsRepository.findRequestsById(requestsId)
                .switchIfEmpty(Mono.error(new RequestsValidationException(
                        "Request with Id: " + requestsId + " not found.")))
                .flatMap(this::validateUpdateRequests)
                .flatMap(request ->
                        validateStatusId(newStatusId, request)
                )
                .doOnError(e -> {
                    logger.severe(String.format("Error al actualizar la solicitud [%d]: %s", requestsId, e.getMessage()));
                });
    }

    private Mono<Requests> validateStatusId(Long newStatusId, Requests request) {
        return statusRepository.findStatusById(newStatusId)
                .switchIfEmpty(Mono.error(new RequestsValidationException(
                        "Invalid status with Id: " + newStatusId)))
                .flatMap(statusValidated -> {
                    return saveRequests(newStatusId, request);
                });
    }

    private Mono<Requests> saveRequests(Long newStatusId, Requests request) {
        request.setStatusId(newStatusId);
        return requestsRepository.saveRequests(request)
                .doOnNext(saved -> logger.info(String.format("Solicitud [%d] actualizada a [%s]",
                        saved.getId(), saved.getLoanTypeName())))
                .flatMap(saved -> {
                    return getStatusAndLoanTypeName(request, saved);
                });
    }

    private Mono<Requests> getStatusAndLoanTypeName(Requests request, Requests saved) {
        Mono<Status> statusMono = statusRepository.findStatusById(request.getStatusId());
        Mono<LoanType> loanTypeMono = loanTypeRepository.findLoanTypeById(request.getLoanTypeId());

        return Mono.zip(statusMono, loanTypeMono)
                .flatMap(tuple -> {
                    Status status = tuple.getT1();
                    LoanType loanType = tuple.getT2();

                    return publishRequestsUpdateToSqs(saved, status, loanType);
                });
    }

    private Mono<Requests> publishRequestsUpdateToSqs(Requests saved, Status status, LoanType loanType) {
        return sqsUseCaseInterface.publishStatusRequest(
                        SqsMessageDTO.builder()
                                .to(saved.getEmail())
                                .subject("El estado de su solicitud de préstamo es " + status.getName())
                                .body(String.format(
                                        "Su solicitud de préstamo de tipo %s ha sido %s.",
                                        loanType.getName(), status.getName()
                                ))
                                .build()
                )
                .doOnSuccess(v -> logger.info(String.format("Evento enviado a SQS para solicitud [%d]", saved.getId())))
                .thenReturn(saved);
    }

    private Mono<Requests> validateUpdateRequests(Requests requests) {
        if (requests.getStatusId() == null || requests.getLoanTypeId() == null) {
            return Mono.error(new RequestsValidationException("Request structure is incomplete (status an loan type)."));
        }

        return Mono.zip(
                statusRepository.findStatusById(requests.getStatusId()),
                loanTypeRepository.findLoanTypeById(requests.getLoanTypeId())
        ).map(tuple -> {
            requests.setStatusName(tuple.getT1().getName());
            requests.setLoanTypeName(tuple.getT2().getName());
            return requests;
        });
    }
}
