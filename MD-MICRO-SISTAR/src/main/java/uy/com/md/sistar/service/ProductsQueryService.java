package uy.com.md.sistar.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.sistar.dto.TarjetasxCIResponse;
import uy.com.md.sistar.dto.in.ProductsRequestDto;
import uy.com.md.sistar.dto.out.AccountResponseDto;
import uy.com.md.sistar.dto.out.ProductInfo;
import uy.com.md.sistar.dto.out.ProductsResponseDto;
import uy.com.md.sistar.soap.SistarClient;
import uy.com.md.sistar.soap.client.TarjetasxCI;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProductsQueryService {

  public static final String GET_PRODUCTS = "getProducts";
  public static final String GET_ACCOUNT_NUM = "getAccountNum";
  private final ModelMapper modelMapper;
  private final SistarClient sistarClientWSMDCustom;
  private final SistarServiceProperties config;
  private final ServiceUtils serviceUtils;

  public ProductsQueryService(ModelMapper modelMapper,
                              @Qualifier("SistarClientWSMDCustom") SistarClient sistarClientWSMDCustom,
                              ServiceUtils serviceUtils,
                              SistarServiceProperties config
  ){
    this.modelMapper = modelMapper;
    this.sistarClientWSMDCustom = sistarClientWSMDCustom;
    this.serviceUtils = serviceUtils;
    this.config = config;
  }

  @CircuitBreaker(name = GET_PRODUCTS, fallbackMethod = "fallback")
  public InternalResponseDto<ProductsResponseDto> getProductos(ProductsRequestDto requestDto) {
    InternalResponseDto<ProductsResponseDto> response = new InternalResponseDto<>();
    ProductsResponseDto respDto = new ProductsResponseDto();
    respDto.getInfo().setProducts(new ArrayList<>());

    // Obtengo productos para cada uno de los id de emisor configurados
    Arrays.stream(config.getIssuers()).forEach(e -> {
      requestDto.setIssuer(Long.parseLong(e));
      List<ProductInfo> productsList = getProductInfo(requestDto, true);
      respDto.getInfo().getProducts().addAll(productsList);
    });

    List<ProductInfo> prods = respDto.getInfo().getProducts();
    if(!prods.isEmpty() && !prods.get(0).getTarjetas().isEmpty()){
      respDto.getInfo().setName1(prods.get(0).getTarjetas().get(0).getName1());
      respDto.getInfo().setName2(prods.get(0).getTarjetas().get(0).getName2());
      respDto.getInfo().setLastName(prods.get(0).getTarjetas().get(0).getLastName1());
      respDto.getInfo().setLastName2(prods.get(0).getTarjetas().get(0).getLastName2());
    }
    String fullName = Stream.of(
      respDto.getInfo().getName1(),
      respDto.getInfo().getName2(),
      respDto.getInfo().getLastName(),
      respDto.getInfo().getLastName2()
    )
    .filter(s -> s != null && !s.isEmpty())
    .collect(Collectors.joining(" "));

    respDto.getInfo().setName(fullName);
    respDto.setSuccess(true);
    response.setRespuesta(respDto);

    return response;
  }

  private InternalResponseDto<ProductsResponseDto> fallback(ProductsRequestDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }

  private List<ProductInfo> getProductInfo(ProductsRequestDto requestDto, boolean enableBlockingCodeFilter) {
    TarjetasxCI requestSoap = modelMapper.map(requestDto, TarjetasxCI.class);
    TarjetasxCIResponse responseSOAP = sistarClientWSMDCustom.getProductos(requestSoap);
    Set<String> prodCodes = new HashSet<>();

    return responseSOAP
        .getTarjetas()
        .stream()
        // Ordeno por fecha de vencimiento descendente
        .sorted(
            Comparator.comparing(TarjetasxCIResponse.TarjetaCIBanco::getVencimiento_AAAAMM).reversed()
                .thenComparing(TarjetasxCIResponse.TarjetaCIBanco::getCodigo_de_Bloqueo)
        )
        // Normalmente si hay algun error o no se encuentran tarjetas, se devuelve un unico item
        // con un codigo y mensaje de error, en ese caso excluyo ese item, y el resultado es una colleccion vacía
        .filter(card -> Integer.parseInt(card.getCodigoError()) == 0)
        // Si esta habilitado el filtrado, filtro las tarjetas con estados de bloqueo configurados
        .filter(card -> !enableBlockingCodeFilter || config.getAvailable_cards_blocking_codes().contains(StringUtils.defaultIfBlank(card.getCodigo_de_Bloqueo(), "")))
        // Me quedo con la primer tarjeta o la que esté en estado normal
        .filter(card -> !prodCodes.contains(String.format("%s:%s", card.getCuenta(), card.getCodigo_Tipo_Producto())) || Integer.parseInt(card.getCodigo_de_Bloqueo()) == 0)
        // Voy llevando registro de cuales son los productos para los que se obtuvo tarjeta
        .map(card -> {
          prodCodes.add(String.format("%s:%s", card.getCuenta(), card.getCodigo_Tipo_Producto()));
          return card;
        })
        // Realizo el mapeo a ProductInfo
        .map(card -> modelMapper.map(card, ProductInfo.class))
        .collect(Collectors.toList());
  }

  protected String getAccountNum(Long issuer, Long product, String country, String document, String documentType){
    ProductsRequestDto request = new ProductsRequestDto();
    request.setDocument(document);
    request.setIssuer(issuer);
    request.setCountry(country);
    request.setDocumentType(documentType);
    request.setProduct(product);

    return Optional.of(getCuenta(request))
        .map(InternalResponseDto::getRespuesta)
        .map(AccountResponseDto::getRealAccount)
        .orElse(null);
  }

  @CircuitBreaker(name = GET_ACCOUNT_NUM, fallbackMethod = "getAccountNumFallback")
  public InternalResponseDto<AccountResponseDto> getCuenta(ProductsRequestDto requestDto) {
    InternalResponseDto<AccountResponseDto> response = new InternalResponseDto<>();
    final List<ProductInfo> productInfo = getProductInfo(requestDto, false);

    final List<ProductInfo> lstProductInfo = productInfo
        .stream()
        .filter(pi -> Objects.nonNull(pi.getProducto()))
        .filter(pi -> Objects.nonNull(pi.getProducto().getCodigo()))
        .filter(pi -> pi.getProducto().getCodigo().equals(requestDto.getProduct()))
        .limit(1)
        .collect(Collectors.toList());
    if (lstProductInfo.isEmpty()) {
      response.setResultado(false);
    } else {
      response.setRespuesta(new AccountResponseDto(lstProductInfo.get(0).getCuenta()));
    }
    return response;
  }

  private InternalResponseDto<AccountResponseDto> getAccountNumFallback(ProductsRequestDto requestDto, Exception ex){
    return serviceUtils.crackResponse(requestDto, ex);
  }

  private InternalResponseDto<ProductsResponseDto> getProductsFallback(ProductsRequestDto requestDto, Exception ex){
    return serviceUtils.crackResponse(requestDto, ex);
  }
}
