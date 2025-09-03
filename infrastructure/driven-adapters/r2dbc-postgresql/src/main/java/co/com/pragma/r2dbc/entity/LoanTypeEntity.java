package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;

@Table("loan_type")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoanTypeEntity {
    @Id
    private Long id;
    @Column("name")
    private String name;
    @Column("min_amount")
    private BigInteger minAmount;
    @Column("max_amount")
    private BigInteger maxAmount;
    @Column("interest_rate")
    private Float interestRate;
    @Column("automatic_validation")
    private Boolean automaticValidation;
}
