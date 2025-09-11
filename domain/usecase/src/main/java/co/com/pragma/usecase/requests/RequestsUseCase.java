package co.com.pragma.usecase.requests;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.requests.Requests;
import co.com.pragma.model.requests.dto.PageCriteria;
import co.com.pragma.model.requests.dto.PagedSummary;
import co.com.pragma.model.requests.dto.RequestsFilter;
import co.com.pragma.model.requests.gateways.RequestsRepository;
import co.com.pragma.model.status.Status;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.usecase.requests.dto.SqsMessageDTO;
import co.com.pragma.usecase.requests.validations.RequestsValidation;
import co.com.pragma.usecase.requests.validations.cases.AmountAndTermValidation;
import co.com.pragma.usecase.requests.validations.cases.EmailValidation;
import co.com.pragma.usecase.requests.validations.cases.RequestsDataValidation;
import co.com.pragma.usecase.requests.validations.cases.UserExistValidation;
import co.com.pragma.usecase.requests.validations.error.RequestsValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class RequestsUseCase implements RequestsUseCaseInterface {

    private static final Logger logger = Logger.getLogger(RequestsUseCase.class.getName());

    private final LoanTypeRepository loanTypeRepository;
    private final StatusRepository statusRepository;
    private final RequestsRepository requestsRepository;
    private final UserUseCaseInterface userUseCaseInterface;
    private final SqsUseCaseInterface sqsUseCaseInterface;

    @Override
    public Mono<Requests> saveRequests(Requests requests) {
        RequestsValidation requestsValidation = new RequestsValidation()
                .includeValidation(new AmountAndTermValidation())
                .includeValidation(new RequestsDataValidation())
                .includeValidation(new EmailValidation())
                .includeValidation(new UserExistValidation(userUseCaseInterface));

        return requestsValidation.validate(requests)
                .then(loanTypeRepository.findLoanTypeById(requests.getLoanTypeId())
                        .switchIfEmpty(Mono.error(new RequestsValidationException("The loan type Id does not exists.")))
                        .flatMap(loanType -> {
                            requests.setLoanTypeId(loanType.getId());
                            return statusRepository.findStatusById(4L) // Se busca si existe el status en la tabla correspondiente
                                    .flatMap(status -> {
                                        status.setId(4L); // Se almacena un status por defecto
                                        requests.setStatusId(status.getId());
                                        return userUseCaseInterface.isValidUser(requests.getIdentityNumber(), requests.getEmail())
                                                .flatMap(isValid -> {
                                                    if (Boolean.FALSE.equals(isValid)) {
                                                        return Mono.error(new RequestsValidationException("User is not valid."));
                                                    }
                                                    Requests requestValidated = requests;
                                                    return requestsRepository.saveRequests(requestValidated);
                                                });
                                    });
                        }).log()
                );
    }

    @Override
    public Flux<Requests> findAllRequests() {
        return requestsRepository.findAllRequests();
    }

    @Override
    public Mono<Requests> findRequestsById(Long id) {
        return requestsRepository.findRequestsById(id);
    }

    @Override
    public Mono<PagedSummary<Requests>> findAllRequestsWithSummary(RequestsFilter filter, PageCriteria page) {
        return requestsRepository.findAllByFilters(filter, page)
                .flatMap(pageSummary ->
                        Flux.fromIterable(pageSummary.request())
                                .flatMap(requestDocument -> {
                                    Mono<Status> status =
                                            statusRepository.findStatusById(requestDocument.getStatusId());

                                    Mono<LoanType> loanType =
                                            loanTypeRepository.findLoanTypeById(requestDocument.getLoanTypeId());

                                    return Mono.zip(status, loanType)
                                            .map(tuple -> {
                                                requestDocument.setStatusId(tuple.getT1().getId());
                                                requestDocument.setLoanTypeId(tuple.getT2().getId());
                                                return requestDocument;
                                            });
                                })
                                .collectList()
                                .map(requestsList -> new PagedSummary<>(
                                        requestsList,
                                        pageSummary.page(),
                                        pageSummary.size(),
                                        pageSummary.total()
                                ))
                );
    }

    @Override
    public Mono<Requests> getRequestsByIdentityNumber(String identityNumber) {
        return requestsRepository.findByIdentityNumber(identityNumber);
    }

    @Override
    public Mono<Requests> getRequestsByEmail(String email) {
        return requestsRepository.findByEmail(email);
    }

    @Override
    public Mono<Requests> updateRequests(Long requestsId, Long newStatusId) {

        logger.info(String.format("Iniciando proceso de actualización de solicitud [%d] al estado [%s]", requestsId, newStatusId));

        return requestsRepository.findRequestsById(requestsId)
                .switchIfEmpty(Mono.error(new RequestsValidationException(
                        "Request with Id: " + requestsId + " not found.")))
                .flatMap(this::validateUpdateRequests)
                .flatMap(request ->
                        statusRepository.findStatusById(newStatusId)
                                .switchIfEmpty(Mono.error(new RequestsValidationException(
                                        "Invalid status with Id: " + newStatusId)))
                                .flatMap(statusValidated -> {
                                    request.setStatusId(newStatusId);
                                    return requestsRepository.saveRequests(request)
                                            .doOnNext(saved -> logger.info(String.format("Solicitud [%d] actualizada a [%s]",
                                                    saved.getId(), saved.getLoanTypeName())))
                                            .flatMap(saved -> {
                                                Mono<Status> statusMono = statusRepository.findStatusById(request.getStatusId());
                                                Mono<LoanType> loanTypeMono = loanTypeRepository.findLoanTypeById(request.getLoanTypeId());

                                                return Mono.zip(statusMono, loanTypeMono)
                                                        .flatMap(tuple -> {
                                                            Status status = tuple.getT1();
                                                            LoanType loanType = tuple.getT2();

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
                                                        });
                                            });
                                })
                )
                .doOnError(e -> {
                    logger.severe(String.format("Error al actualizar la solicitud [%d]: %s", requestsId, e.getMessage()));
                });
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
