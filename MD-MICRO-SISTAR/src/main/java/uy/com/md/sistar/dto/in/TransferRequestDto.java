package uy.com.md.sistar.dto.in;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uy.com.md.sistar.dto.AccountInfoDeserializer;

@Data
public class TransferRequestDto {
  
  @Data
  @JsonDeserialize(using = AccountInfoDeserializer.class)
  public static abstract class GenericAccount {
    Long cuenta;
  }
  
  @EqualsAndHashCode(callSuper = true)
  @Data
  @JsonDeserialize()
  public static class MDAccount extends GenericAccount{
    Long emisor;
  }
  
  @EqualsAndHashCode(callSuper = true)
  @Data
  @JsonDeserialize()
  public static class BankAccount extends GenericAccount {
    String nombre;
    String id;
  }
  
  @EqualsAndHashCode(callSuper = true)
  @Data
  @JsonDeserialize()
  public static class ExternalProcessorAccount extends GenericAccount {
    String codigoDestino;
  }

  @Data
  @JsonDeserialize()
  public static class BeneficiaryAccount{
    String nombre;
    BankAccount banco;
    Long cuenta;
    Long emisor;
    Long cuentaDestino;
    String codigoDestino;
  }

  String canal;
  MDAccount ordenante;
  Integer moneda;
  Integer marca;
  BigDecimal monto;
  String motivo;
  String referencia;
  String comprobante;
  String rrn;
  BeneficiaryAccount beneficiario;
  Integer sucursal;
  Integer cuenta; // corresponde a la cuenta del beneficiario de la transferencia. Es una alternativa a indicar
                  // en numero de cuenta en beneficiario.cuenta
}
