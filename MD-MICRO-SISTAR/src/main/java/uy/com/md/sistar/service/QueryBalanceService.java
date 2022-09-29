package uy.com.md.sistar.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeOrigen;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Origenes;
import uy.com.md.sistar.dto.in.AccountInfoRequestDto;
import uy.com.md.sistar.dto.in.AccountVpsRequestDto;
import uy.com.md.sistar.dto.in.BalanceRequestDto;
import uy.com.md.sistar.dto.out.AccountInfoResponseDto;
import uy.com.md.sistar.dto.out.Balance;
import uy.com.md.sistar.dto.out.BalanceResponseDto;
import uy.com.md.sistar.soap.SistarClient;
import uy.com.md.sistar.soap.client.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static uy.com.md.sistar.util.SistarConstants.UNKNOWN_ERR_CODE;

@Service
public class QueryBalanceService {

  private static final Logger logger = LoggerFactory.getLogger(QueryBalanceService.class);
  public static final String GET_BALANCE = "getBalance";
  private final ModelMapper modelMapper;
  private final SistarClient sistarClientWSMidinero;
  private final SistarServiceProperties config;
  private final MensajeService messageService;
  private final SistarClient sistarClientMDF25;
  private final ServiceUtils serviceUtils;

  public QueryBalanceService(ModelMapper modelMapper,
                             @Qualifier("SistarClientWSMidinero") SistarClient sistarClientWSMidinero,
                             @Qualifier("SistarClientMDF25")SistarClient sistarClientMDF25,
                             SistarServiceProperties config,
                             MensajeService messageService,
                             ServiceUtils serviceUtils
                             ){
    this.modelMapper = modelMapper;
    this.sistarClientWSMidinero = sistarClientWSMidinero;
    this.sistarClientMDF25 = sistarClientMDF25;
    this.config = config;
    this.messageService = messageService;
    this.serviceUtils = serviceUtils;
  }

  @CircuitBreaker(name = GET_BALANCE, fallbackMethod = "fallback")
  public InternalResponseDto<BalanceResponseDto> balance(BalanceRequestDto requestDto) {
    BalanceResponseDto respData = new BalanceResponseDto();
    respData.setSuccess(false);

    ConsultaDisponibleSistarbanc requestSoap = modelMapper.map(requestDto, ConsultaDisponibleSistarbanc.class);
    ConsultaDisponibleSistarbancResponse responseSOAP = sistarClientWSMidinero.balance(requestSoap);
    Optional<ConsultaDisponibleSistarbancResponse> resp = Optional.ofNullable(responseSOAP);

    Optional<Long> err = resp
        .map(ConsultaDisponibleSistarbancResponse::getConsultaDisponibleSistarbancResult)
        .map(AccountInfo::getErrorCodi)
        .map(Long::parseLong);

    Optional<AccountCreditLineLocal> availableLocalCurrency = resp
        .map(ConsultaDisponibleSistarbancResponse::getConsultaDisponibleSistarbancResult)
        .map(AccountInfo::getAccountCreditLineLocal)
        .map(ArrayOfAccountCreditLineLocal::getAccountCreditLineLocal)
        .map(e -> e.get(0));

    Optional<AccountCreditLineExtranjera> availableForeignCurrency = resp
        .map(ConsultaDisponibleSistarbancResponse::getConsultaDisponibleSistarbancResult)
        .map(AccountInfo::getAccountCreditLineExtranjera)
        .map(ArrayOfAccountCreditLineExtranjera::getAccountCreditLineExtranjera)
        .map(e -> e.get(0));

    boolean success = err.isPresent() && err.get() == 0;

    if (success) {
      BalanceResponseDto.Info balanceInfoLocal = availableLocalCurrency.map(accountCreditLineLocal -> modelMapper.map(accountCreditLineLocal, BalanceResponseDto.Info.class)).orElse(null);
      BalanceResponseDto.Info balanceInfoForeign = availableForeignCurrency.map(accountCreditLineExtranjera -> modelMapper.map(accountCreditLineExtranjera, BalanceResponseDto.Info.class)).orElse(null);

      ArrayList<BalanceResponseDto.Info> arr = new ArrayList<>();

      if(balanceInfoLocal != null) {
        if(Boolean.TRUE.equals(config.getSet_negative_balance_to_cero())){
          setBalanceToCero(balanceInfoLocal.getBalances());
        }
        arr.add(balanceInfoLocal);
      }

      if(balanceInfoForeign != null){
        if(Boolean.TRUE.equals(config.getSet_negative_balance_to_cero())){
          setBalanceToCero(balanceInfoForeign.getBalances());
        }
        arr.add(balanceInfoForeign);
      }

      respData.setInfo(arr);
      AccountVpsRequestDto accExceptionRequestDto = new AccountVpsRequestDto();
      AccountInfoRequestDto accCardRequestDto = new AccountInfoRequestDto();
      accCardRequestDto.setRealAccount(requestDto.getAccount());

      SistarbancMDF25STATUSCUENTATARJETAS requestCardSoap = modelMapper.map(accCardRequestDto, SistarbancMDF25STATUSCUENTATARJETAS.class);
      SistarbancMDF25STATUSCUENTATARJETASResponse responseCardSoap = sistarClientMDF25.obtenerInfoCuenta(requestCardSoap);
      success = NumberUtils.toInt(responseCardSoap.getCoderror(), -1) == 0;

      if(success){
        AccountInfoResponseDto mappedResponse = modelMapper.map(responseCardSoap, AccountInfoResponseDto.class);

        accExceptionRequestDto.setRealAccount(Long.valueOf(requestDto.getAccount()));
        accExceptionRequestDto.setMarca(Long.valueOf(mappedResponse.getInfo().getProducto().getSello()));
        accExceptionRequestDto.setEmisor(Integer.valueOf(mappedResponse.getInfo().getProducto().getEmisor()));

        ExceptionFraudeConsul exceptionRequest = modelMapper.map(accExceptionRequestDto, ExceptionFraudeConsul.class);
        ExcepcionFraudeConsulResult exceptions = sistarClientWSMidinero.estadoVps(exceptionRequest).getExceptionFraudeConsulResult();
        success = exceptions.getErrorCode() == 0 && exceptions.getListaExcepciones() != null;

        if(success){
          for(EFConsul consul : exceptions.getListaExcepciones().getEFConsul()){
            if(consul.getMoneCodi() == 1){
              assert balanceInfoLocal != null;
              balanceInfoLocal.setExcepciones(true);
              balanceInfoLocal.setTefId(consul.getTefId());
            }
            else {
              assert balanceInfoForeign != null;
              balanceInfoForeign.setExcepciones(true);
              balanceInfoForeign.setTefId(consul.getTefId());
            }
          }
        }
      }

      respData.setSuccess(true);
      return new InternalResponseDto<>(respData);
    } else {
      String errCode = resp.isPresent() ? resp.get().getConsultaDisponibleSistarbancResult().getErrorCodi(): UNKNOWN_ERR_CODE;
      throw new SistarbancOperationException(errCode, "Ocurrió un error al solicitar balance a API Sistarbanc");
    }
  }

  private void setBalanceToCero(List<Balance> balances){
    for(Balance currentBalance : balances){
      if (currentBalance.getBalance() < 0)
        currentBalance.setBalance((double) 0);
    }
  }

  private InternalResponseDto<BalanceResponseDto> fallback(BalanceRequestDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }
}
