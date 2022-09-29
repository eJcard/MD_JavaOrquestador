package uy.com.md.sistar.controller;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Mono;
import uy.com.md.common.in.PagoServiciosRequestDto;
import uy.com.md.common.movimientos.InfoDto;
import uy.com.md.common.out.PagoServiciosResponse;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.sistar.dto.in.*;
import uy.com.md.sistar.dto.out.*;
import uy.com.md.sistar.util.SistarConstants;

@RequestMapping(
    path = "/sistar",
    produces = MediaType.APPLICATION_JSON_VALUE
)
public interface Controller {

  @PostMapping("/cards")
  ResponseEntity<InternalResponseDto<NewCardResponseDto>> solicitudProductoNominado(
      @RequestBody NewCardRequestDto requestDto
  );

  @PostMapping("/cards/credits")
  ResponseEntity<InternalResponseDto<CreditResponseDto>> recarga(
      @RequestBody
          CreditRequestDto requestDto
  );

  @RequestMapping(
      path = "/cards/transfers",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  ResponseEntity<InternalResponseDto<TransferResponseDto>> transfer(
      @RequestBody
          TransferRequestDto requestDto
  );

  @PostMapping("/cards/transactions/confirm/{rrn}")
  ResponseEntity<InternalResponseDto<TransferResponseDto>> transferConfirmUpdate(
      @PathVariable
          String rrn,

      @RequestBody
          TransferUpdateRequestDto requestDto
  );

  @PostMapping("/cards/transactions/reverse/{rrn}")
  ResponseEntity<InternalResponseDto<TransferResponseDto>> transferReverseUpdate(
      @PathVariable
          String rrn,

      @RequestBody
          TransferUpdateRequestDto requestDto
  );

  @GetMapping(path = "/cardholders/{compositeId}/products")
  @Operation(summary = "Get list of cards for this cardholder",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Successful operation",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  examples = @ExampleObject(
                      name = "successfulGetProductsExample",
                      ref = "#/components/examples/successfulGetProductsExample"
                  )
              )
          )
      }
  )
  ResponseEntity<InternalResponseDto<ProductsResponseDto>> getProductos(
      @Parameter(required = true, example = "858.2.42588947")
      @Pattern(regexp = SistarConstants.CARDHOLDER_COMPOSITE_ID_PATTERN)
      @PathVariable
          String compositeId
  );

  @GetMapping("/accounts/{realAccount}/transactions")
  ResponseEntity<InternalResponseDto<InfoDto>> getTransactions(

      @Parameter(example = SistarConstants.ACCOUNT_NUMBER_EXAMPLE, required = true)
      @Size(min = 10, max = 18)
      @Pattern(regexp = SistarConstants.ACCOUNT_NUMBER_PATTERN)
      @PathVariable
          String realAccount,

      @Parameter(example = "2020-12-01")
      @Pattern(regexp = SistarConstants.DATE_FORMAT)
          String start,

      @Parameter(example = "2020-11-01")
      @Pattern(regexp = SistarConstants.DATE_FORMAT)
          String end,

      @Parameter(example = "100.01")
          String itc,

      @Parameter(example = "asc")
      @Schema(allowableValues = {"asc", "desc"})
      @RequestParam(defaultValue = "desc")
          String order,

      @Parameter(example = "10")
      @Min(0)
      @Max(50)
      @RequestParam(defaultValue = "5")
          Integer length,

      @Parameter(example = "2")
      @Min(1)
      @RequestParam(defaultValue = "1")
          Integer offset,

      @Parameter(example = "858")
          Integer currency
  );

  @PostMapping("/transactions/reverse/{rrn}")
  ResponseEntity<InternalResponseDto<ReverseResponseDto>> reverso(
      @RequestBody
          ReverseRequestDto requestDto,

      @PathVariable
          String rrn
  );

  @GetMapping("/transactions/{rrn}")
  ResponseEntity<InternalResponseDto<TransactionInfoResponseDto>> buscar(
      @RequestParam(defaultValue = "0") String requestRrn,
      @RequestParam("emisor") String issuer,
      @PathVariable("rrn") String transactionRrn
  );

  @GetMapping("/accounts/{realAccount}/balances")
  ResponseEntity<InternalResponseDto<BalanceResponseDto>> balance(
      @Parameter(example = "370100000113", required = true)
      @Size(min = 10, max = 18)
      @Pattern(regexp = SistarConstants.ACCOUNT_NUMBER_PATTERN)
      @PathVariable
          String realAccount
  );

  @PostMapping("/cards/unnamed")
  ResponseEntity<InternalResponseDto<NewCardResponseDto>> solicitudProductoInnominado(@RequestBody NewCardRequestDto requestDto);

  @PostMapping("/cards/debits")
  ResponseEntity<InternalResponseDto<PagoServiciosResponse>> solicitarPago(@RequestBody PagoServiciosRequestDto requestDto);

  @PostMapping("/cards/unnamedReplacement")
  ResponseEntity<InternalResponseDto<CardsResponseDto>> reemplazoProductoInnominado(@RequestBody ReemplazoRequestDto requestDto);

  @PostMapping("/cards/unnamedRenew")
  ResponseEntity<InternalResponseDto<CardsResponseDto>> renovacionProductoInnominado(@RequestBody ReemplazoRequestDto requestDto);

  @GetMapping(path = "/cardholders/{compositeId}/issuers/{issuer}/products/{product}/account")
  ResponseEntity<InternalResponseDto<AccountResponseDto>> getNroCuenta(
      @Parameter(required = true, example = "858.2.42588947")
      @Pattern(regexp = SistarConstants.CARDHOLDER_COMPOSITE_ID_PATTERN)
      @PathVariable String compositeId,
      @Parameter(required = true)
      @Pattern(regexp = SistarConstants.NUMERIC_PATTERN)
      @PathVariable String issuer,
      @Parameter(required = true)
      @Pattern(regexp = SistarConstants.NUMERIC_PATTERN)
      @PathVariable String product
  );

  @PutMapping("/cards/{realAccount}")
  ResponseEntity<InternalResponseDto<CardsResponseDto>> actualizarEstadoTarjeta(
      @Parameter(example = "370100000113", required = true)
      @Size(min = 10, max = 18)
      @Pattern(regexp = SistarConstants.ACCOUNT_NUMBER_PATTERN)
      @PathVariable
          String realAccount,

      @RequestBody
          CardStatusUpdateDto requestDto);

  @GetMapping("/customers/{realAccount}")
  ResponseEntity<InternalResponseDto<AccountInfoDto>> obtenerInfoCuenta(
      @Parameter(example = "370100000113", required = true)
      @Size(min = 10, max = 18)
      @Pattern(regexp = SistarConstants.ACCOUNT_NUMBER_PATTERN)
      @PathVariable
          String realAccount
  );

  @PutMapping("/cards/{realAccount}/vps/enable")
  ResponseEntity<InternalResponseDto<AccountVPSResponseDto>> habilitarParametrosSeguridad(
      @Parameter(example = "370100000113", required = true)
      @Size(min = 10, max = 18)
      @Pattern(regexp = SistarConstants.ACCOUNT_NUMBER_PATTERN)
      @PathVariable
          Long realAccount,
      @RequestParam(name = "marca", defaultValue = "3")
          Long brand,
      @RequestParam(name = "emisor", defaultValue = "37")
          Integer issuer,
      @RequestParam(name = "currency")
          Integer currency);


  @PutMapping("/cards/{realAccount}/vps/disable")
  ResponseEntity<InternalResponseDto<AccountVPSResponseDto>> deshabilitarParametrosSeguridad(
      @Parameter(example = "370100000113", required = true)
      @Size(min = 10, max = 18)
      @Pattern(regexp = SistarConstants.ACCOUNT_NUMBER_PATTERN)
      @PathVariable
          Long realAccount,
      @RequestParam(name = "currency")
          Integer currency,
      @RequestParam(name = "marca", defaultValue = "3")
          Long brand,
      @RequestParam(name = "emisor", defaultValue = "37")
          Integer issuer,
      @RequestBody
          AccountVpsRequestDto requestDto);

  @PostMapping("/cards/{realAccount}/vps")
  ResponseEntity<InternalResponseDto<AccountVPSResponseDto>> altaParametrosSeguridad(
      @Parameter(example = "370100000113", required = true)
      @Size(min = 10, max = 18)
      @Pattern(regexp = SistarConstants.ACCOUNT_NUMBER_PATTERN)
      @PathVariable
          Long realAccount,
      @RequestBody
          AccountVpsRequestDto requestDto);

  @GetMapping("/cards/{realAccount}/vps")
  ResponseEntity<InternalResponseDto<AccountVPSResponseDto>> parametrosSeguridad(
      @Parameter(example = "370100000113", required = true)
      @Size(min = 10, max = 18)
      @Pattern(regexp = SistarConstants.ACCOUNT_NUMBER_PATTERN)
      @PathVariable
          Long realAccount,
      @RequestParam(name = "currency")
          Integer currency,
      @RequestParam(name = "marca", defaultValue = "37")
          Long brand,
      @RequestParam(name = "emisor", defaultValue = "3")
          Integer issuer);

}
