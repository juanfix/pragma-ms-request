package co.com.pragma.model.requests;
import co.com.pragma.model.loantype.LoanType;
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
public class Requests {
    private Long id;
    private Long amount;
    private Integer term;
    private String name;
    private String email;
    private String identityNumber;
    private Long baseSalary;
    private Long statusId;
    private String statusName;
    private Long loanTypeId;
    private String loanTypeName;
    private Float monthlyAmount;
    private LoanType loanType;

}
