package co.com.pragma.usecase.requests;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.requests.Requests;
import co.com.pragma.model.requests.gateways.RequestsRepository;
import co.com.pragma.model.status.Status;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.usecase.requests.dto.SqsEmailMessageDTO;
import co.com.pragma.usecase.requests.dto.SqsReportMessageDTO;
import co.com.pragma.usecase.requests.interfaces.SqsUseCaseInterface;
import co.com.pragma.usecase.requests.validations.error.RequestsValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class UpdateRequestsUseCaseTest {
    private RequestsRepository requestsRepository;
    private LoanTypeRepository loanTypeRepository;
    private StatusRepository statusRepository;
    private UpdateRequestsUseCase updateRequestsUseCase;
    private SqsUseCaseInterface sqsUseCaseInterface;

    @BeforeEach
    void setUp(){
        requestsRepository = mock(RequestsRepository.class);
        loanTypeRepository = mock(LoanTypeRepository.class);
        statusRepository = mock(StatusRepository.class);
        sqsUseCaseInterface = mock(SqsUseCaseInterface.class);
        updateRequestsUseCase = new UpdateRequestsUseCase(loanTypeRepository, statusRepository, requestsRepository, sqsUseCaseInterface);
    }

    @Test
    void shouldUpdateAStatusRequest() {
        Requests requests = Requests.builder()
                .id(1L)
                .amount(6000000L)
                .term(12)
                .email("juan@mail.com")
                .identityNumber("123456")
                .statusId(4L)
                .loanTypeId(1L)
                .build();

        LoanType loanType = new LoanType(1L, "Libre inversion", 3000000L, 25000000L,15.0F, Boolean.TRUE);

        Status status = new Status(1L, "Pendiente", "Solicitud en espera para revisar");

        when(requestsRepository.findRequestsById(requests.getId()))
                .thenReturn(Mono.just(requests));
        when(loanTypeRepository.findLoanTypeById(any()))
                .thenReturn(Mono.just(loanType));
        when(statusRepository.findStatusById(any()))
                .thenReturn(Mono.just(status));
        when(sqsUseCaseInterface.publishStatusRequest(any(), any(SqsEmailMessageDTO.class)))
                .thenReturn(Mono.empty());
        when(sqsUseCaseInterface.publishReportRequest(any(), any(SqsReportMessageDTO.class)))
                .thenReturn(Mono.empty());
        when(requestsRepository.saveRequests(requests, Boolean.TRUE))
                .thenAnswer(invocation -> {
                    Requests u = invocation.getArgument(0);
                    u.setLoanTypeId(loanType.getId());
                    return Mono.just(u);
                });

        StepVerifier.create(updateRequestsUseCase.execute(requests.getId(), 1L))
                .expectNextMatches(r -> r.getStatusId().equals(1L) &&
                        r.getLoanTypeId() != null &&
                        r.getLoanTypeId().equals(1L))
                .verifyComplete();

        ArgumentCaptor<Requests> captor = ArgumentCaptor.forClass(Requests.class);
        verify(requestsRepository).saveRequests(captor.capture(), any());
        Requests savedRequest = captor.getValue();
        assertEquals("juan@mail.com", savedRequest.getEmail());
        assertEquals(1L, savedRequest.getStatusId());
    }

    @Test
    void shouldFailWhenUpdateAStatusIdIsNull() {
        Requests requests = Requests.builder()
                .id(1L)
                .amount(6000000L)
                .term(12)
                .email("juan@mail.com")
                .identityNumber("123456")
                .statusId(null)
                .loanTypeId(1L)
                .build();

        LoanType loanType = new LoanType(1L, "Libre inversion", 3000000L, 25000000L,15.0F, Boolean.TRUE);

        Status status = new Status(1L, "Pendiente", "Solicitud en espera para revisar");

        when(requestsRepository.findRequestsById(requests.getId()))
                .thenReturn(Mono.just(requests));
        when(loanTypeRepository.findLoanTypeById(any()))
                .thenReturn(Mono.just(loanType));
        when(statusRepository.findStatusById(any()))
                .thenReturn(Mono.just(status));
        when(sqsUseCaseInterface.publishStatusRequest(any(), any(SqsEmailMessageDTO.class)))
                .thenReturn(Mono.empty());
        when(requestsRepository.saveRequests(requests, Boolean.TRUE))
                .thenAnswer(invocation -> {
                    Requests u = invocation.getArgument(0);
                    u.setLoanTypeId(loanType.getId());
                    return Mono.just(u);
                });

        StepVerifier.create(updateRequestsUseCase.execute(requests.getId(), 1L))
                .expectErrorMatches(throwable -> throwable instanceof RequestsValidationException)
                .verify();
    }
}
