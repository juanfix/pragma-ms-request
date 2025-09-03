package co.com.pragma.model.loantype;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanType {
    private Long id;
    private String name;
    private Long minAmount;
    private Long maxAmount;
    private Float interestRate;
    private Boolean automaticValidation;
}
