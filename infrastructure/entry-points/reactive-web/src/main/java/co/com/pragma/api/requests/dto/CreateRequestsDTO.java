package co.com.pragma.api.requests.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreateRequestsDTO {
    private Long amount;
    private Integer term;
    private String identityNumber;
    private String email;
    private Long statusId;
    private Long loanTypeId;
}
