package co.com.pragma.model.loantype;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanTypeTest {
    @Test
    void shouldCreateALoanTypeWithArgs() {
        Long id = 1L;
        String name = "Libre inversion";
        Long minAmount = 1L;
        Long maxAmount = 6L;
        Float interestRate = 12.2F;

        LoanType loanType = new LoanType(id, name, minAmount, maxAmount, interestRate, false);

        assertNotNull(loanType);
        assertEquals(id, loanType.getId());
        assertEquals(name, loanType.getName());
        assertEquals(minAmount, loanType.getMinAmount());
        assertEquals(maxAmount, loanType.getMaxAmount());
        assertEquals(interestRate, loanType.getInterestRate());
        assertEquals(false, loanType.getAutomaticValidation());
    }

    @Test
    void shouldCreateALoanTypeWithBuilder() {
        Long id = 1L;
        String name = "Libre inversion";
        Long minAmount = 1L;
        Long maxAmount = 6L;
        Float interestRate = 12.2F;

        LoanType loanType = LoanType.builder()
                .id(id)
                .name(name)
                .minAmount(minAmount)
                .maxAmount(maxAmount)
                .interestRate(interestRate)
                .automaticValidation(false)
                .build();

        assertNotNull(loanType);
        assertEquals(id, loanType.getId());
        assertEquals(name, loanType.getName());
        assertEquals(minAmount, loanType.getMinAmount());
        assertEquals(maxAmount, loanType.getMaxAmount());
        assertEquals(interestRate, loanType.getInterestRate());
        assertEquals(false, loanType.getAutomaticValidation());
    }

    @Test
    void shouldCreateALoanTypeWithSetters() {
        Long id = 1L;
        String name = "Libre inversion";
        Long minAmount = 1L;
        Long maxAmount = 6L;
        Float interestRate = 12.2F;

        LoanType loanType = new LoanType();
        loanType.setId(id);
        loanType.setName(name);
        loanType.setMinAmount(minAmount);
        loanType.setMaxAmount(maxAmount);
        loanType.setInterestRate(interestRate);
        loanType.setAutomaticValidation(false);

        assertNotNull(loanType);
        assertEquals(id, loanType.getId());
        assertEquals(name, loanType.getName());
        assertEquals(minAmount, loanType.getMinAmount());
        assertEquals(maxAmount, loanType.getMaxAmount());
        assertEquals(interestRate, loanType.getInterestRate());
        assertEquals(false, loanType.getAutomaticValidation());
    }
}
