package uy.com.md.sistar.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.sistar.dto.in.AccountInfoRequestDto;
import uy.com.md.sistar.dto.out.AccountInfoResponseDto;
import uy.com.md.sistar.soap.SistarClient;
import uy.com.md.sistar.soap.client.SistarbancMDF25STATUSCUENTATARJETAS;
import uy.com.md.sistar.soap.client.SistarbancMDF25STATUSCUENTATARJETASResponse;

@Service
public class AccountInfoService {

  public static final String GET_ACCOUNT_INFO = "getAccountInfo";
  private final ModelMapper modelMapper;
  private final SistarClient sistarClientMDF25;
  private final ServiceUtils serviceUtils;

  public AccountInfoService(ModelMapper modelMapper,
                            @Qualifier("SistarClientMDF25") SistarClient sistarClientMDF25,
                            ServiceUtils serviceUtils
  ){
    this.modelMapper = modelMapper;
    this.sistarClientMDF25 = sistarClientMDF25;
    this.serviceUtils = serviceUtils;
  }

  @CircuitBreaker(name = GET_ACCOUNT_INFO, fallbackMethod = "fallback")
  public InternalResponseDto<AccountInfoResponseDto> getAccountInfo(AccountInfoRequestDto requestDto) {
    InternalResponseDto<AccountInfoResponseDto> response = null;
    SistarbancMDF25STATUSCUENTATARJETAS requestSoap;

    requestSoap = modelMapper.map(requestDto, SistarbancMDF25STATUSCUENTATARJETAS.class);
    SistarbancMDF25STATUSCUENTATARJETASResponse responseSoap = sistarClientMDF25.obtenerInfoCuenta(requestSoap);
    if(responseSoap.getCoderror().equals("00")){
      AccountInfoResponseDto mappedResponse = modelMapper.map(responseSoap, AccountInfoResponseDto.class);
      response = new InternalResponseDto<>(mappedResponse);
    }else{
      throw new SistarbancOperationException(responseSoap.getCoderror(), responseSoap.getReason());
    }
    return response;
  }

  private InternalResponseDto<AccountInfoResponseDto> fallback(AccountInfoRequestDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }
}
