package uy.com.md.sistar.Schemas.json.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionInfoResponseDto {
    boolean success;
    String authorizationCode;
    String id;
    String account;
    @JsonProperty("description")
    String desc;
    String currency;
    BigDecimal amount;
    String branchOffice;
    String settlementDate;
    String valueDate;
    String settlementTime;
    String sbCode;
    String sbSubCode;
    String type;
}
