package uy.com.md.sistar.soap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.support.MarshallingUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import lombok.SneakyThrows;
import uy.com.md.sistar.config.SistarConfig;
import uy.com.md.sistar.dto.TarjetasxCIResponse;
import uy.com.md.sistar.service.SistarServiceProperties;
import uy.com.md.sistar.soap.client.*;
import uy.com.md.sistar.soap.client2.SistarbancMDTrfC2BAUTORIZACION;
import uy.com.md.sistar.soap.client2.SistarbancMDTrfC2BAUTORIZACIONResponse;
import uy.com.md.sistar.soap.client2.SistarbancMDTrfC2BCONFIRMACION;
import uy.com.md.sistar.soap.client2.SistarbancMDTrfC2BCONFIRMACIONResponse;
import uy.com.md.sistar.soap.client2.SistarbancMDTrfC2BREVERSO;
import uy.com.md.sistar.soap.client2.SistarbancMDTrfC2BREVERSOResponse;
import uy.com.md.sistar.util.LogXML;
import uy.com.md.sistar.util.SistarConstants;

public class SistarClient extends WebServiceGatewaySupport {
   @Autowired
   SistarConfig config;

   @Autowired
   SistarServiceProperties properties;

   public SistarbancMDF2ABMCUENTATARJETAResponse solicitudProductoNominado(SistarbancMDF2ABMCUENTATARJETA requestSoap) {
      new LogXML<>(SistarbancMDF2ABMCUENTATARJETA.class).log(requestSoap);
      SistarbancMDF2ABMCUENTATARJETAResponse responseSOAP = (SistarbancMDF2ABMCUENTATARJETAResponse) getWebServiceTemplate().marshalSendAndReceive(
            requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_ABM_CUENTA));
      new LogXML<>(SistarbancMDF2ABMCUENTATARJETAResponse.class).log(responseSOAP);
      return responseSOAP;
   }

   public WSPagosAjustesxCIResponse recargaXCI(WSPagosAjustesxCI requestSoap) {
      new LogXML<>(WSPagosAjustesxCI.class).log(requestSoap);
      WSPagosAjustesxCIResponse responseSOAP = (WSPagosAjustesxCIResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap,
            message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_RECARGA_CI));
      new LogXML<>(WSPagosAjustesxCIResponse.class).log(responseSOAP);
      return responseSOAP;
   }

   public WSPagosAjustesResponse recargaXCta(WSPagosAjustes requestSoap) {
      new LogXML<>(WSPagosAjustes.class).log(requestSoap);
      WSPagosAjustesResponse responseSOAP = (WSPagosAjustesResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap,
            message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_RECARGA_CTA));
      new LogXML<>(WSPagosAjustesResponse.class).log(responseSOAP);
      return responseSOAP;
   }

   @SneakyThrows
   public SistarbancMDV2MOVENPERIODOResponse movimientos(SistarbancMDV2MOVENPERIODO requestSoap) {
      SistarbancMDV2MOVENPERIODOResponse result = new SistarbancMDV2MOVENPERIODOResponse();
      result.setMovenperiodo(new MOVENPERIODO());

      boolean loadMockData = (config.getActiveProfile().equals("dev") || config.getActiveProfile().equals("uat")) &&
         properties.getEnable_mock() && 
         properties.getMock_account().contains(requestSoap.getAccountnumber());

      if(!loadMockData){
         result = (SistarbancMDV2MOVENPERIODOResponse) getWebServiceTemplate()
             .marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_GET_MOVIMIENTOS));
      }else{
         InputStream stream = null;
         String fileName = String.format("mock/movimientos-%s.xml", requestSoap.getAccountnumber());
         File file = new File(fileName);
         if(!file.exists()){
            // busco en el directorio del modulo. Util cuando corro en ambiente de desarrollo
            file = new File("MD-MICRO-SISTAR/"+fileName);
         }
         if(file.exists()){
            try{
               stream = new FileInputStream(file);
      
               // obtengo el contenido del body contenido en el mensaje SOAP
               SOAPMessage msg = MessageFactory.newInstance().createMessage(null, stream);
               Document doc = msg.getSOAPBody().extractContentAsDocument();
               result = (SistarbancMDV2MOVENPERIODOResponse) getUnmarshaller().unmarshal(new DOMSource(doc));
      
               List<MOVENPERIODOITEM> movs = result.getMovenperiodo().getMOVENPERIODOITEM()
               .stream()
               .limit(requestSoap.getCantmovsmax())
               .collect(Collectors.toList());
               result.setMovenperiodo(new MOVENPERIODO(){{movenperiodoitem = movs;}});
            }
            finally{
               stream.close();
            }
         }
      }
      return result;
   }

   public TarjetasxCIResponse getProductos(TarjetasxCI requestSoap) {
      new LogXML<>(TarjetasxCI.class).log(requestSoap);
      
      TarjetasxCIResponse response = getWebServiceTemplate().sendAndReceive(message -> {
         SoapMessage soapMessage = ((SoapMessage) message);
         MarshallingUtils.marshal(getMarshaller(), requestSoap, message);
         soapMessage.setSoapAction(SistarConstants.SOAP_ACTION_GET_PRODUCTOS);
      }, message -> {
         TarjetasxCIResponse response1;
         SoapMessage soapMessage = ((SoapMessage) message);
         Node cardsNode = soapMessage
               .getDocument()
               .getFirstChild()
               .getFirstChild()
               .getFirstChild()
               .getFirstChild()
               .getFirstChild()
               .getNextSibling()
               .getFirstChild();

         response1 = (TarjetasxCIResponse) getUnmarshaller().unmarshal(new DOMSource(cardsNode));

         return response1;
      });
      new LogXML<>(TarjetasxCIResponse.class).log(response);

      return response;
   }

   public WSPagosAjustesxCIResponse reversoPorDocumento(WSPagosAjustesxCI requestSoap) {
      new LogXML<>(WSPagosAjustesxCI.class).log(requestSoap);
      WSPagosAjustesxCIResponse responseSoap = (WSPagosAjustesxCIResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_REVERSO_POR_DOC));
      new LogXML<>(WSPagosAjustesxCIResponse.class).log(responseSoap);
      return responseSoap;
   }

   public WSPagosAjustesResponse reversoPorCuenta(WSPagosAjustes requestSoap) {
      new LogXML<>(WSPagosAjustes.class).log(requestSoap);
      WSPagosAjustesResponse responseSoap = (WSPagosAjustesResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_REVERSO_POR_CTA));
      new LogXML<>(WSPagosAjustesResponse.class).log(responseSoap);
      return responseSoap;
   }

   public ConsultaDisponibleSistarbancResponse balance(ConsultaDisponibleSistarbanc requestSoap) {
      return (ConsultaDisponibleSistarbancResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_DISPONIBLE));
   }

   public CrearTarjetaPrepagaInnominadaResponse solicitudProductoInnominado(CrearTarjetaPrepagaInnominada requestSoap) {
      new LogXML<>(CrearTarjetaPrepagaInnominada.class).log(requestSoap);
      CrearTarjetaPrepagaInnominadaResponse responseSOAP = (CrearTarjetaPrepagaInnominadaResponse) getWebServiceTemplate().marshalSendAndReceive(
            requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_ALTA_INNOMINADA));
      new LogXML<>(CrearTarjetaPrepagaInnominadaResponse.class).log(responseSOAP);
      return responseSOAP;
   }

   public ReemplazoTarjetaPrepagaInnominadaXCiResponse reemplazoProductoInnominadoXCI(ReemplazoTarjetaPrepagaInnominadaXCi requestSoap) {
      return (ReemplazoTarjetaPrepagaInnominadaXCiResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_REEMPLAZO_INNOMINADA_XCI));
   }

   public RenovacionTarjetaPrepagaInnominadaXCiResponse renovacionProductoInnominadoXCI(RenovacionTarjetaPrepagaInnominadaXCi requestSoap) {
      return (RenovacionTarjetaPrepagaInnominadaXCiResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_RENOVACION_INNOMINADA_XCI));
   }

   public ReemplazoTarjetaPrepagaInnominadaResponse reemplazoProductoInnominadoXCuenta(ReemplazoTarjetaPrepagaInnominada requestSoap) {
      return (ReemplazoTarjetaPrepagaInnominadaResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_REEMPLAZO_INNOMINADA_XCUENTA));
   }

   public TransferenciaC2CResponse transferC2C(TransferenciaC2C requestSoap) {
      new LogXML<>(TransferenciaC2C.class).log(requestSoap);
      TransferenciaC2CResponse responseSoap = (TransferenciaC2CResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_C2C_TRANSFERENCIA));
      new LogXML<>(TransferenciaC2CResponse.class).log(responseSoap);
      return responseSoap;
   }

   public RevertirTransferenciaC2CResponse reversarTransferC2C(RevertirTransferenciaC2C requestSoap) {
      new LogXML<>(RevertirTransferenciaC2C.class).log(requestSoap);
      RevertirTransferenciaC2CResponse responseSoap = (RevertirTransferenciaC2CResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_C2C_REVERSO));
      new LogXML<>(RevertirTransferenciaC2CResponse.class).log(responseSoap);
      return responseSoap;
   }

   public SistarbancMDTrfC2BAUTORIZACIONResponse autorizarTransferC2B(SistarbancMDTrfC2BAUTORIZACION requestSoap) {
      new LogXML<>(SistarbancMDTrfC2BAUTORIZACION.class).log(requestSoap);
      SistarbancMDTrfC2BAUTORIZACIONResponse responseSoap = (SistarbancMDTrfC2BAUTORIZACIONResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_C2B_AUTORIZACION));
      new LogXML<>(SistarbancMDTrfC2BAUTORIZACIONResponse.class).log(responseSoap);
      return responseSoap;
   }

   public SistarbancMDTrfC2BCONFIRMACIONResponse confirmarTransferC2B(SistarbancMDTrfC2BCONFIRMACION requestSoap) {
      new LogXML<>(SistarbancMDTrfC2BCONFIRMACION.class).log(requestSoap);
      SistarbancMDTrfC2BCONFIRMACIONResponse responseSoap = (SistarbancMDTrfC2BCONFIRMACIONResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_C2B_CONFIRMACION));
      new LogXML<>(SistarbancMDTrfC2BCONFIRMACIONResponse.class).log(responseSoap);
      return responseSoap;
   }

   public SistarbancMDTrfC2BREVERSOResponse reversarTransferC2B(SistarbancMDTrfC2BREVERSO requestSoap) {
      return (SistarbancMDTrfC2BREVERSOResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_C2B_REVERSO));
   }

  public SistarbancMDF2CAMBIOESTADOTARJETAResponse cambiarEstadoTarjeta(SistarbancMDF2CAMBIOESTADOTARJETA requestSoap) {
   return (SistarbancMDF2CAMBIOESTADOTARJETAResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_ACTUALIZAR_ESTADO_TARJETA));
  }

  public WSCambioEstadoCuentaExecuteResponse cambiarEstadoTarjeta(WSCambioEstadoCuentaExecute requestSoap) {
   return (WSCambioEstadoCuentaExecuteResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_HABILITAR_INHABILITAR));
  }

  public SistarbancMDF25STATUSCUENTATARJETASResponse obtenerInfoCuenta(SistarbancMDF25STATUSCUENTATARJETAS requestSoap) {
   return (SistarbancMDF25STATUSCUENTATARJETASResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_CONSULTA_ESTADO_TARJETA));
  }

  public PagoServicioCajaPrepagaResponse solicitarPago(PagoServicioCajaPrepaga requestSoap) {
     return (PagoServicioCajaPrepagaResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_PAGO_SERVICIO_CAJA));
   }
   
   public SistarbancMDF25ECHOTESTResponse fitoEchoTest() {
      SistarbancMDF25ECHOTEST requestSoap = new SistarbancMDF25ECHOTEST();
      return (SistarbancMDF25ECHOTESTResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_FITO_ECHO));
   }
   
   public TasaDelDiaDolaresResponse getCotizacionDelDia() {
      TasaDelDiaDolares requestSoap = new TasaDelDiaDolares();
      return (TasaDelDiaDolaresResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_TASA_DEL_DIA_DOLARES));
   }

    public ConsultaPorReferenciaResponse obtenerInfoTransaccion(ConsultaPorReferencia requestSoap) {
       return (ConsultaPorReferenciaResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_CONSULTA_TRANSACCION));
    }

   public ExceptionFraudeModiResponse cambioEstadoVps(ExceptionFraudeModi requestSoap) {
      return (ExceptionFraudeModiResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> {
         ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_EXCEPTION_FRAUDE_MODI);
      });
   }

   public ExceptionFraudeAltaResponse altaVps(ExceptionFraudeAlta requestSoap) {
      return (ExceptionFraudeAltaResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> {
         ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_EXCEPTION_FRAUDE_ALTA);
      });
   }

   public ExceptionFraudeConsulResponse estadoVps(ExceptionFraudeConsul requestSoap) {
      return (ExceptionFraudeConsulResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> {
         ((SoapMessage) message).setSoapAction(SistarConstants.SOAP_ACTION_EXCEPTION_FRAUDE_CONSUL);
      });
   }
}
