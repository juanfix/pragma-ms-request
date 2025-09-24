package co.com.pragma.usecase.requests.dto;


import co.com.pragma.model.requests.Requests;

import java.util.List;

public record LambdaDebtCapacityRequestDTO(
        Long totalIncome,
        List<Requests> activeLoans,
        Loan newLoan,
        String email
) {
    public record Loan(
        Long id,
        Long amount,
        Float interestRate,
        Integer term,
        Boolean automaticValidation
    ) {

    }
}
