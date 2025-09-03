package co.com.pragma.model.loantype.gateways;

import co.com.pragma.model.loantype.LoanType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanTypeRepository {
    Flux<LoanType> findAllLoanTypes();
    Mono<LoanType> findLoanTypeById(Long id);
}
