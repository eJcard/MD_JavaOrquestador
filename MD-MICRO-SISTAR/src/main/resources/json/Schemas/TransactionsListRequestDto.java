package uy.com.md.sistar.Schemas.json.in;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionsListRequestDto {

   String realAccount;

   String start;

   String end;

   String itc;

   String order;

   Integer length;

   Integer offset;

   Integer currency;
}
