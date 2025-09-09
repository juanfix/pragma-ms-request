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
import co.com.pragma.usecase.requests.validations.error.RequestsValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RequestsUseCaseTest {
    private RequestsRepository requestsRepository;
    private LoanTypeRepository loanTypeRepository;
    private StatusRepository statusRepository;
    private RequestsUseCase requestsUseCase;
    private UserUseCaseInterface userUseCaseInterface;

    @BeforeEach
    void setUp(){
        requestsRepository = mock(RequestsRepository.class);
        loanTypeRepository = mock(LoanTypeRepository.class);
        statusRepository = mock(StatusRepository.class);
        userUseCaseInterface = mock(UserUseCaseInterface.class);
        requestsUseCase = new RequestsUseCase(loanTypeRepository, statusRepository, requestsRepository, userUseCaseInterface);

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
        when(requestsRepository.saveRequests(requests))
                .thenAnswer(invocation -> {
                    Requests u = invocation.getArgument(0);
                    u.setLoanTypeId(loanType.getId());
                    return Mono.just(u);
                });

        StepVerifier.create(requestsUseCase.saveRequests(requests))
                .expectNextMatches(r -> r.getEmail().equals("juan@mail.com") &&
                        r.getLoanTypeId() != null &&
                        r.getLoanTypeId().equals(1L))
                .verifyComplete();

        ArgumentCaptor<Requests> captor = ArgumentCaptor.forClass(Requests.class);
        verify(requestsRepository).saveRequests(captor.capture());
        Requests savedRequest = captor.getValue();
        assertEquals("juan@mail.com", savedRequest.getEmail());
        assertEquals(1L, savedRequest.getLoanTypeId());
    }

    @Test
    void shouldFindAllRequests() {
        Requests request1 = Requests.builder()
                .id(1L)
                .amount(6000000L)
                .term(12)
                .email("juan@mail.com")
                .identityNumber("123456")
                .build();

        Requests request2 = Requests.builder()
                .id(2L)
                .amount(5000000L)
                .term(10)
                .email("juanjo@mail.com")
                .identityNumber("654321")
                .build();

        when(requestsRepository.findAllRequests()).thenReturn(Flux.just(request1, request2));

        StepVerifier.create(requestsUseCase.findAllRequests())
                .expectNext(request1)
                .expectNext(request2)
                .verifyComplete();

        verify(requestsRepository, times(1)).findAllRequests();
    }

    @Test
    void shouldFindRequestByIdentityNumber() {
        String identityNumber = "123";
        Requests user = Requests.builder()
                .id(2L)
                .amount(5000000L)
                .term(10)
                .email("juanjo@mail.com")
                .identityNumber("123")
                .build();

        when(requestsRepository.findByIdentityNumber(identityNumber)).thenReturn(Mono.just(user));

        StepVerifier.create(requestsUseCase.getRequestsByIdentityNumber(identityNumber))
                .expectNext(user)
                .verifyComplete();

        verify(requestsRepository, times(1)).findByIdentityNumber(identityNumber);
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

        StepVerifier.create(requestsUseCase.saveRequests(requests))
                .expectErrorMatches(throwable -> throwable instanceof RequestsValidationException &&
                        throwable.getMessage().equals("The loan type Id does not exists."))
                .verify();

        verify(requestsRepository, never()).saveRequests(requests);
    }

    @Test
    void shouldListPendingApplicationsWithPagination() {
        Requests requests = new Requests();
        requests.setIdentityNumber("123");
        requests.setStatusId(1L);
        requests.setStatusName("Pendiente de revision");
        requests.setLoanTypeId(1L);
        requests.setLoanTypeName("Libre inversion");

        Status status = new Status();
        status.setId(1L);
        status.setName("Pendiente de revision");

        LoanType loanType = new LoanType();
        loanType.setId(1L);
        loanType.setName("Libre inversion");

        PagedSummary<Requests> mockPage = new PagedSummary<>(
                List.of(requests),
                1, // page
                10, // size
                1L   // total
        );

        RequestsFilter requestsFilter = new RequestsFilter(status.getId(), loanType.getId());

        when(requestsRepository.findAllByFilters(requestsFilter, new PageCriteria(1, 10)))
                .thenReturn(Mono.just(mockPage));

        when(statusRepository.findStatusById(status.getId()))
                .thenReturn(Mono.just(status));

        when(loanTypeRepository.findLoanTypeById(loanType.getId()))
                .thenReturn(Mono.just(loanType));

        StepVerifier.create(requestsUseCase.findAllRequestsWithSummary(requestsFilter, new PageCriteria(1, 10)))
                .assertNext(page -> {
                    assertThat(page.request()).hasSize(1);
                    Requests request = page.request().get(0);
                    assertThat(request.getLoanTypeId()).isNotNull();
                    assertThat(request.getLoanTypeName()).isEqualTo("Libre inversion");
                    assertThat(request.getStatusId()).isNotNull();
                    assertThat(request.getStatusName()).isEqualTo("Pendiente de revision");

                    assertThat(page.page()).isEqualTo(1);
                    assertThat(page.size()).isEqualTo(10);
                    assertThat(page.total()).isEqualTo(1L);
                })
                .verifyComplete();

        verify(requestsRepository).findAllByFilters(requestsFilter, new PageCriteria(1, 10));
        verify(statusRepository).findStatusById(1L);
        verify(loanTypeRepository).findLoanTypeById(1L);
    }

}
