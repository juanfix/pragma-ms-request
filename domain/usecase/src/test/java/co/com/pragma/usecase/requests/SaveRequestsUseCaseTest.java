package co.com.pragma.usecase.requests;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.requests.Requests;
import co.com.pragma.model.requests.gateways.RequestsRepository;
import co.com.pragma.model.status.Status;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.usecase.requests.interfaces.UserUseCaseInterface;
import co.com.pragma.usecase.requests.validations.error.RequestsValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SaveRequestsUseCaseTest {
    private RequestsRepository requestsRepository;
    private LoanTypeRepository loanTypeRepository;
    private StatusRepository statusRepository;
    private SaveRequestsUseCase saveRequestsUseCase;
    private UserUseCaseInterface userUseCaseInterface;

    @BeforeEach
    void setUp(){
        requestsRepository = mock(RequestsRepository.class);
        loanTypeRepository = mock(LoanTypeRepository.class);
        statusRepository = mock(StatusRepository.class);
        userUseCaseInterface = mock(UserUseCaseInterface.class);
        saveRequestsUseCase = new SaveRequestsUseCase(loanTypeRepository, statusRepository, requestsRepository, userUseCaseInterface);
    }

    @Test
    void shouldCreateANewRequest() {
        Requests requests = Requests.builder()
                .id(1L)
                .amount(6000000L)
                .term(12)
                .email("juan@mail.com")
                .identityNumber("123456")
                .build();

        LoanType loanType = new LoanType(1L, "Libre inversion", 3000000L, 25000000L,15.0F, Boolean.TRUE);

        Status status = new Status(1L, "Pendiente", "Solicitud en espera para revisar");

        when(requestsRepository.findByEmail("juan@mail.com"))
                .thenReturn(Mono.empty());
        when(loanTypeRepository.findLoanTypeById(any()))
                .thenReturn(Mono.just(loanType));
        when(statusRepository.findStatusById(any()))
                .thenReturn(Mono.just(status));
        when(userUseCaseInterface.isValidUser("123456", "juan@mail.com"))
                .thenReturn(Mono.just(true));
        when(requestsRepository.saveRequests(requests, Boolean.FALSE))
                .thenAnswer(invocation -> {
                    Requests u = invocation.getArgument(0);
                    u.setLoanTypeId(loanType.getId());
                    return Mono.just(u);
                });

        StepVerifier.create(saveRequestsUseCase.execute(requests))
                .expectNextMatches(r -> r.getEmail().equals("juan@mail.com") &&
                        r.getLoanTypeId() != null &&
                        r.getLoanTypeId().equals(1L))
                .verifyComplete();

        ArgumentCaptor<Requests> captor = ArgumentCaptor.forClass(Requests.class);
        verify(requestsRepository).saveRequests(captor.capture(), any());
        Requests savedRequest = captor.getValue();
        assertEquals("juan@mail.com", savedRequest.getEmail());
        assertEquals(1L, savedRequest.getLoanTypeId());
    }

    @Test
    void shouldFailCreateWhenUserIsNotValid() {
        Requests requests = Requests.builder()
                .id(1L)
                .amount(6000000L)
                .term(12)
                .email("juan@mail.com")
                .identityNumber("123456")
                .build();

        LoanType loanType = new LoanType(1L, "Libre inversion", 3000000L, 25000000L,15.0F, Boolean.TRUE);

        Status status = new Status(1L, "Pendiente", "Solicitud en espera para revisar");

        when(requestsRepository.findByEmail("juan@mail.com"))
                .thenReturn(Mono.empty());
        when(loanTypeRepository.findLoanTypeById(any()))
                .thenReturn(Mono.just(loanType));
        when(statusRepository.findStatusById(any()))
                .thenReturn(Mono.just(status));
        when(userUseCaseInterface.isValidUser("123456", "juan@mail.com"))
                .thenReturn(Mono.just(false));
        when(requestsRepository.saveRequests(requests, Boolean.FALSE))
                .thenAnswer(invocation -> {
                    Requests u = invocation.getArgument(0);
                    u.setLoanTypeId(loanType.getId());
                    return Mono.just(u);
                });

        StepVerifier.create(saveRequestsUseCase.execute(requests))
                .expectErrorMatches(throwable -> throwable instanceof RequestsValidationException)
                .verify();
    }

    @Test
    void shouldFailsWhenLoanTypeDoesNotExist() {
        Requests requests = Requests.builder()
                .id(1L)
                .amount(6000000L)
                .term(12)
                .email("juan@mail.com")
                .identityNumber("123456")
                .build();

        when(requestsRepository.findByEmail(requests.getEmail()))
                .thenReturn(Mono.empty());
        when(loanTypeRepository.findLoanTypeById(any()))
                .thenReturn(Mono.empty());
        when(userUseCaseInterface.isValidUser("123456", "juan@mail.com"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(saveRequestsUseCase.execute(requests))
                .expectErrorMatches(throwable -> throwable instanceof RequestsValidationException &&
                        throwable.getMessage().equals("The loan type Id does not exists."))
                .verify();

        verify(requestsRepository, never()).saveRequests(requests, Boolean.FALSE);
    }

}
