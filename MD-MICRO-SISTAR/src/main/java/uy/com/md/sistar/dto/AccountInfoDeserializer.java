package uy.com.md.sistar.dto;

import org.springframework.social.dto.ser.UniquePropertyPolymorphicDeserializer;

import uy.com.md.sistar.dto.in.TransferRequestDto.BankAccount;
import uy.com.md.sistar.dto.in.TransferRequestDto.ExternalProcessorAccount;
import uy.com.md.sistar.dto.in.TransferRequestDto.GenericAccount;
import uy.com.md.sistar.dto.in.TransferRequestDto.MDAccount;

public class AccountInfoDeserializer extends UniquePropertyPolymorphicDeserializer<GenericAccount> {
  
  private static final long serialVersionUID = -5137353139256658415L;

  public AccountInfoDeserializer() {
    super(GenericAccount.class);
    this.register("codigoDestino", ExternalProcessorAccount.class);
    this.register("id", BankAccount.class);
    this.register("*", MDAccount.class);
  }
}
