package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;

@Table("requests")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RequestsEntity {
    @Id
    private Long id;
    @Column("amount")
    private BigInteger amount;
    @Column("term")
    private Integer term;
    @Column("email")
    private String email;
    @Column("identity_number")
    private String identityNumber;
    @Column("status_id")
    private Long statusId;
    @Column("loan_type_id")
    private Long loanTypeId;

    @Transient
    private StatusEntity status;
    @Transient
    private LoanTypeEntity loanType;
}
