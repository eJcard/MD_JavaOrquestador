package uy.com.md.sistar.Schemas.json.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInfoRequestDto {
    private String rrnRequest;
    private String rrnTransaction;
    private String emisor;
}
