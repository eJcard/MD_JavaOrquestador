package uy.com.md.sistar.controller;

import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uy.com.md.common.in.PagoServiciosRequestDto;
import uy.com.md.common.movimientos.InfoDto;
import uy.com.md.common.out.PagoServiciosResponse;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeOrigen;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Origenes;
import uy.com.md.sistar.dto.in.*;
import uy.com.md.sistar.dto.out.*;
import uy.com.md.sistar.service.*;
import uy.com.md.sistar.util.SistarMappingUtils;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static uy.com.md.sistar.util.SistarConstants.UNKNOWN_ERR_CODE;

@RefreshScope
@RestController
public class SistarController implements Controller {

   private final AccountInfoService accountInfoService;
   private final C2BService c2BService;
   private final C2CService c2CService;
   private final CardCreditService cardCreditService;
   private final CardDebitPINService cardDebitPINService;
   private final CardDebitService cardDebitService;
   private final CardRenewalService cardRenewalService;
   private final CardReplaceService cardReplaceService;
   private final CardUpdateService cardUpdateService;
   private final LimitsControlService limitsControlService;
   private final ProductsQueryService productsQueryService;
   private final QueryBalanceService queryBalanceService;
   private final ReversalService reversalService;
   private final C2PService c2pService;
   QueryTransactionsService queryTransactionsService;
   CardCreationService cardCreationService;
   final MensajeService mensajeService;

   @Autowired
   SistarMappingUtils mappingUtils;

   @Autowired
   SistarServiceProperties properties;

   public SistarController(AccountInfoService accountInfoService,
                           C2BService c2BService,
                           C2CService c2CService,
                           C2PService c2PService,
                           CardCreationService cardCreationService,
                           CardCreditService cardCreditService,
                           CardDebitPINService cardDebitPINService,
                           CardDebitService cardDebitService,
                           CardRenewalService cardRenewalService,
                           CardReplaceService cardReplaceService,
                           CardUpdateService cardUpdateService,
                           LimitsControlService limitsControlService,
                           ProductsQueryService productsQueryService,
                           QueryBalanceService queryBalanceService,
                           QueryTransactionsService queryTransactionsService,
                           ReversalService reversalService,
                           MensajeService mensajeService
   ) {
      this.accountInfoService = accountInfoService;
      this.c2BService = c2BService;
      this.c2CService = c2CService;
      this.c2pService = c2PService;
      this.cardCreationService = cardCreationService;
      this.cardCreditService = cardCreditService;
      this.cardDebitPINService = cardDebitPINService;
      this.cardDebitService = cardDebitService;
      this.cardRenewalService = cardRenewalService;
      this.cardReplaceService = cardReplaceService;
      this.cardUpdateService = cardUpdateService;
      this.limitsControlService = limitsControlService;
      this.productsQueryService = productsQueryService;
      this.queryBalanceService = queryBalanceService;
      this.queryTransactionsService = queryTransactionsService;
      this.reversalService = reversalService;
      this.mensajeService = mensajeService;
   }

   @SneakyThrows
   @Override
   public ResponseEntity<InternalResponseDto<NewCardResponseDto>> solicitudProductoNominado(NewCardRequestDto requestDto) {
      return new ResponseEntity<>(cardCreationService.createNominated(requestDto), HttpStatus.OK);
   }

   @Override
   public ResponseEntity<InternalResponseDto<CreditResponseDto>> recarga(CreditRequestDto requestDto) {
      InternalResponseDto<CreditResponseDto> response;
      if (StringUtils.isBlank(requestDto.getCuenta())) {
         try {
               response = cardCreditService.recargaXCI(requestDto);
         } catch (Exception e) {
            response = new InternalResponseDto<>(null);
            response.setResultado(false);
            response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.SISTAR, e.getMessage(), e.getMessage())));
         }
      } else {
         response = cardCreditService.recargaXCta(requestDto);
      }
      return new ResponseEntity<>(response, HttpStatus.OK);
   }

   @Override
   public ResponseEntity<InternalResponseDto<ProductsResponseDto>> getProductos(String compositeId) {
      InternalResponseDto<ProductsResponseDto> response;
      try {
         ProductsRequestDto requestDto = getDTO(compositeId);
         return new ResponseEntity<>(productsQueryService.getProductos(requestDto), HttpStatus.OK);
      } catch (Exception e) {
         response = new InternalResponseDto<>(null);
         response.setResultado(false);
         response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.SISTAR, UNKNOWN_ERR_CODE, e.getCause().toString())));
         return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      }
   }

   @Override
   public ResponseEntity<InternalResponseDto<InfoDto>> getTransactions(String realAccount, String start, String end, String itc,
         String order, Integer length, Integer offset, Integer currency) {
      TransactionsListRequestDto requestDto = new TransactionsListRequestDto(realAccount, start, end, itc, order, length, offset, currency);
      InternalResponseDto<InfoDto> response;
      try {
         return new ResponseEntity(queryTransactionsService.getTransactions(requestDto), HttpStatus.OK);
      } catch (Exception e) {
         response = new InternalResponseDto<>(null);
         response.setResultado(false);
         response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.SISTAR, "999", e.getCause().toString())));
         return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      }
   }

   @Override
   public ResponseEntity<InternalResponseDto<ReverseResponseDto>> reverso(ReverseRequestDto requestDto, String rrn) {
      requestDto.setToReverseRrn(rrn);
      return new ResponseEntity<>(reversalService.reverso(requestDto), HttpStatus.OK);
   }

   @Override
   public ResponseEntity<InternalResponseDto<TransactionInfoResponseDto>> buscar(String requestRrn, String issuer, String transactionRrn) {
      TransactionInfoRequestDto requestDto = new TransactionInfoRequestDto(requestRrn, transactionRrn, issuer);
      return new ResponseEntity<>(queryTransactionsService.getTransaction(requestDto), HttpStatus.OK);
   }

   @Override
   public ResponseEntity<InternalResponseDto<BalanceResponseDto>> balance(String realAccount) {
      BalanceRequestDto request = new BalanceRequestDto();
      request.setAccount(realAccount);
      return new ResponseEntity<>(queryBalanceService.balance(request), HttpStatus.OK);
   }

   private ProductsRequestDto getDTO(String compositeId) {
      ProductsRequestDto req = new ProductsRequestDto();
      String[] values = StringUtils.split(compositeId, '.');
      final int len = ArrayUtils.getLength(values);
      switch (len) {
         case 4:
            req.setIssuer(NumberUtils.toLong(values[3]));
         case 3:
            req.setDocument(StringUtils.defaultIfBlank(values[2], ""));
         case 2:
            req.setDocumentType(StringUtils.defaultIfBlank(values[1], ""));
         case 1:
            req.setCountry(StringUtils.defaultIfBlank(values[0], ""));
      }
      return req;
   }

   public ResponseEntity<InternalResponseDto<NewCardResponseDto>> solicitudProductoInnominado(
         @RequestBody NewCardRequestDto requestDto) {
      return new ResponseEntity<>(cardCreationService.solicitudProductoInnominado(requestDto), HttpStatus.OK);
   }

   public ResponseEntity<InternalResponseDto<CardsResponseDto>> reemplazoProductoInnominado(
         @RequestBody ReemplazoRequestDto requestDto) {
      return new ResponseEntity<>(cardReplaceService.reemplazoProductoInnominado(requestDto), HttpStatus.OK);
   }

   public ResponseEntity<InternalResponseDto<CardsResponseDto>> renovacionProductoInnominado(
         @RequestBody ReemplazoRequestDto requestDto) {
      return new ResponseEntity<>(cardRenewalService.renovacionProductoInnominado(requestDto), HttpStatus.OK);
   }

   @Override
   public ResponseEntity<InternalResponseDto<AccountResponseDto>> getNroCuenta(String compositeId, String issuer, String product) {
      try {
         ProductsRequestDto productsRequestDto = getDTO(compositeId);
         productsRequestDto.setIssuer(NumberUtils.toLong(issuer));
         productsRequestDto.setProduct(NumberUtils.toLong(product));
         return new ResponseEntity<>(productsQueryService.getCuenta(productsRequestDto), HttpStatus.OK);
      } catch (Exception e) {
         InternalResponseDto<AccountResponseDto> response = new InternalResponseDto<>(null);
         response.setResultado(false);
         response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.SISTAR, e.getMessage(), e.getMessage())));
         return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      }
   }

   @Override
   public ResponseEntity<InternalResponseDto<TransferResponseDto>> transfer(TransferRequestDto requestDto) {
      InternalResponseDto<TransferResponseDto> response;
      HttpStatus status;

      switch (requestDto.getCanal()){
         case "debitosAutomaticos":
         case "otrosProcesadores":
            response = c2pService.transfer(requestDto);
            break;
         case "transferenciaBanco":
            response = c2BService.authorize(requestDto);
            break;
         case "midinero":
            response = c2CService.transfer(requestDto);
            break;
         default:
            response = new InternalResponseDto<>(null);
            response.setResultado(false);
            response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.SISTAR, "", "El canal especificado no es válido. Se espera midinero o transferenciaBanco.")));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      }
      status = response.isResultado() ? HttpStatus.OK: HttpStatus.BAD_REQUEST;
      return new ResponseEntity<>(response, status);
   }

   @Override
   public ResponseEntity<InternalResponseDto<TransferResponseDto>> transferConfirmUpdate(String rrn, TransferUpdateRequestDto requestDto){
      requestDto.setRrn(rrn);
      return new ResponseEntity<>(c2BService.confirm(requestDto), HttpStatus.OK);
   }

   @Override
   public ResponseEntity<InternalResponseDto<TransferResponseDto>> transferReverseUpdate(String rrn, TransferUpdateRequestDto requestDto){
      requestDto.setRrn(rrn);
      return new ResponseEntity<>(c2BService.reverse(requestDto), HttpStatus.OK);
   }

   @Override
   public ResponseEntity<InternalResponseDto<CardsResponseDto>> actualizarEstadoTarjeta(String realAccount, CardStatusUpdateDto requestDto) {
      requestDto.setAccount(realAccount);
      return new ResponseEntity<>(cardUpdateService.actualizarEstadoTarjeta(requestDto), HttpStatus.OK);
   }

   @Override
   public ResponseEntity<InternalResponseDto<PagoServiciosResponse>> solicitarPago(PagoServiciosRequestDto requestDto) {
      return new ResponseEntity<>(cardDebitPINService.authorize(requestDto), HttpStatus.OK);
   }

   @Override
   public ResponseEntity<InternalResponseDto<AccountInfoDto>> obtenerInfoCuenta(
         @Size(min = 10, max = 18) @Pattern(regexp = "^\\d{10,18}$") String realAccount) {
      AccountInfoRequestDto requestDto = new AccountInfoRequestDto();
      requestDto.setRealAccount(realAccount);
      InternalResponseDto<AccountInfoResponseDto> fullResponse = accountInfoService.getAccountInfo(requestDto);
      AccountInfoResponseDto data = fullResponse.getRespuesta();
      InternalResponseDto<AccountInfoDto> ret = new InternalResponseDto<>(data == null ? null: data.getInfo());
      ret.setMensajes(fullResponse.getMensajes());
      ret.setResultado(fullResponse.isResultado());
      return new ResponseEntity<>(ret, HttpStatus.OK);
   }

   @Override
   public ResponseEntity<InternalResponseDto<AccountVPSResponseDto>> habilitarParametrosSeguridad(Long realAccount, Long brand, Integer issuer, Integer currency) {
      AccountVpsRequestDto requestDto = new AccountVpsRequestDto();
      requestDto.setRealAccount(realAccount);
      requestDto.setEnable(true);
      requestDto.setMarca(brand);
      requestDto.setEmisor(issuer);
      requestDto.setMoneda(currency);
      return new ResponseEntity<>(limitsControlService.updateLimit(requestDto), HttpStatus.OK);
   }

   @Override
   public ResponseEntity<InternalResponseDto<AccountVPSResponseDto>> deshabilitarParametrosSeguridad(Long realAccount, Integer currency, Long brand, Integer issuer, AccountVpsRequestDto requestDto) {
      requestDto.setRealAccount(realAccount);
      requestDto.setEnable(false);
      requestDto.setMarca(brand);
      requestDto.setEmisor(issuer);
      requestDto.setMoneda(currency);
      String tzExp = ".*([+-]\\d{2}:?\\d{2}|Z)$";
      if(!properties.getFrom_field_default_offset().isEmpty() && !requestDto.getInicio().matches(tzExp)){
         requestDto.setInicio(requestDto.getInicio() + properties.getFrom_field_default_offset());
      }
      if(!properties.getFrom_field_default_offset().isEmpty() && !requestDto.getFin().matches(tzExp)){
         requestDto.setFin(requestDto.getFin() + properties.getFrom_field_default_offset());
      }
      return new ResponseEntity<>(limitsControlService.updateLimit(requestDto), HttpStatus.OK);
   }

   @Override
   public ResponseEntity<InternalResponseDto<AccountVPSResponseDto>> altaParametrosSeguridad(Long realAccount, uy.com.md.sistar.dto.in.AccountVpsRequestDto requestDto) {
      requestDto.setRealAccount(realAccount);
      requestDto.setEnable(true);
      return new ResponseEntity<>(limitsControlService.altaVps(requestDto), HttpStatus.OK);
   }

   @Override
   public ResponseEntity<InternalResponseDto<AccountVPSResponseDto>> parametrosSeguridad(Long realAccount, Integer currency, Long brand, Integer issuer) {
      AccountVpsRequestDto requestDto = new AccountVpsRequestDto();
      requestDto.setRealAccount(realAccount);
      requestDto.setMarca(brand);
      requestDto.setMoneda(currency);
      requestDto.setEmisor(issuer);
      return new ResponseEntity<>(limitsControlService.consultaVps(requestDto), HttpStatus.OK);
   }
}
