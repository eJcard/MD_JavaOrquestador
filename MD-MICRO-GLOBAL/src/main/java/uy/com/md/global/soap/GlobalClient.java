package uy.com.md.global.soap;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;
import uy.com.md.global.soap.client.aamdsoapautorizarpago.AmdSoapAutorizarPagoExecute;
import uy.com.md.global.soap.client.aamdsoapautorizarpago.AmdSoapAutorizarPagoExecuteResponse;
import uy.com.md.global.soap.client.aamdsoapentreganominada.AmdSoapEntregaNominadaExecute;
import uy.com.md.global.soap.client.aamdsoapentreganominada.AmdSoapEntregaNominadaExecuteResponse;
import uy.com.md.global.soap.client.aamdsoapobtenerdatosxcuenta.AmdSoapObtenerDatosXCuentaExecute;
import uy.com.md.global.soap.client.aamdsoapobtenerdatosxcuenta.AmdSoapObtenerDatosXCuentaExecuteResponse;
import uy.com.md.global.soap.client.aamdsoapobtenermismovimientos.AmdSoapObtenerMisMovimientosV2Execute;
import uy.com.md.global.soap.client.aamdsoapobtenermismovimientos.AmdSoapObtenerMisMovimientosV2ExecuteResponse;
import uy.com.md.global.soap.client.aamdsoaprecargargp.AmdSoapRecargarGPExecute;
import uy.com.md.global.soap.client.aamdsoaprecargargp.AmdSoapRecargarGPExecuteResponse;
import uy.com.md.global.soap.client.aamdsoaprecargartarjeta.AmdSoapRecargarTarjetaExecute;
import uy.com.md.global.soap.client.aamdsoaprecargartarjeta.AmdSoapRecargarTarjetaExecuteResponse;
import uy.com.md.global.soap.client.aamdsoaptransferirc2banco.AmdSoapTransferirC2BancoExecute;
import uy.com.md.global.soap.client.aamdsoaptransferirc2banco.AmdSoapTransferirC2BancoExecuteResponse;
import uy.com.md.global.soap.client.aamdsoaptransferirc2c.AmdSoapTransferirC2CExecute;
import uy.com.md.global.soap.client.aamdsoaptransferirc2c.AmdSoapTransferirC2CExecuteResponse;
import uy.com.md.global.soap.client.solicitudproductonominado.AmdSoapSolicitudProductoNominadoV2Execute;
import uy.com.md.global.soap.client.solicitudproductonominado.AmdSoapSolicitudProductoNominadoV2ExecuteResponse;
import uy.com.md.global.util.Constantes;

public class GlobalClient extends WebServiceGatewaySupport {

    public AmdSoapSolicitudProductoNominadoV2ExecuteResponse solicitud(String token, Short sucursal, String caja, String uuid, AmdSoapSolicitudProductoNominadoV2Execute requestSoap) {
        return (AmdSoapSolicitudProductoNominadoV2ExecuteResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> {
            TransportContext context = TransportContextHolder.getTransportContext();
            HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
            ((SoapMessage) message).setSoapAction(Constantes.GLOBAL_SOAP_ACTION_SOLICITUD);
            connection.addRequestHeader(Constantes.AUTH_TOKEN, token);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_SUBAGENCIA, String.valueOf(sucursal));
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_CAJA, caja);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_UUID, uuid);
            connection.addRequestHeader(Constantes.AMD_TOKEN, token);
        });
    }

    public AmdSoapEntregaNominadaExecuteResponse entrega(String token, Short sucursal, String caja, String uuid, AmdSoapEntregaNominadaExecute requestSoap) {
        return (AmdSoapEntregaNominadaExecuteResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> {
            TransportContext context = TransportContextHolder.getTransportContext();
            HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
            ((SoapMessage) message).setSoapAction(Constantes.GLOBAL_SOAP_ACTION_ENTREGA);
            connection.addRequestHeader(Constantes.AUTH_TOKEN, token);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_SUBAGENCIA, String.valueOf(sucursal));
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_CAJA, caja);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_UUID, uuid);
            connection.addRequestHeader(Constantes.AMD_TOKEN, token);
        });
    }

    public AmdSoapRecargarTarjetaExecuteResponse recarga(String token, Short sucursal, String caja, String uuid, AmdSoapRecargarTarjetaExecute requestSoap) {
        return (AmdSoapRecargarTarjetaExecuteResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> {
            TransportContext context = TransportContextHolder.getTransportContext();
            HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
            ((SoapMessage) message).setSoapAction(Constantes.GLOBAL_SOAP_ACTION_RECARGA);
            connection.addRequestHeader(Constantes.AUTH_TOKEN, token);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_SUBAGENCIA, String.valueOf(sucursal));
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_CAJA, caja);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_UUID, uuid);
            connection.addRequestHeader(Constantes.AMD_TOKEN, token);
        });
    }

    public AmdSoapObtenerMisMovimientosV2ExecuteResponse movimientos(String token, Short sucursal, String caja, String uuid, AmdSoapObtenerMisMovimientosV2Execute requestSoap) {
        return (AmdSoapObtenerMisMovimientosV2ExecuteResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> {
            TransportContext context = TransportContextHolder.getTransportContext();
            HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
            ((SoapMessage) message).setSoapAction(Constantes.GLOBAL_SOAP_ACTION_MOVIMIENTOS);
            connection.addRequestHeader(Constantes.AUTH_TOKEN, token);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_SUBAGENCIA, String.valueOf(sucursal));
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_CAJA, caja);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_UUID, uuid);
            connection.addRequestHeader(Constantes.AMD_TOKEN, token);
        });
    }

    public AmdSoapTransferirC2CExecuteResponse transferirSaldosC2C(String token, String uuid, String amdToken, String amdSistema, AmdSoapTransferirC2CExecute requestSoap) {
        return (AmdSoapTransferirC2CExecuteResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> {
            TransportContext context = TransportContextHolder.getTransportContext();
            HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
            ((SoapMessage) message).setSoapAction(Constantes.GLOBAL_SOAP_ACTION_TRANSFERENCIA_C2C);
            connection.addRequestHeader(Constantes.AUTH_TOKEN, token);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_AMD_REQUEST_ID, uuid);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_AMD_SISTEMA, amdSistema);
            connection.addRequestHeader(Constantes.AMD_TOKEN, token);
        });
    }

    public AmdSoapAutorizarPagoExecuteResponse pagoServicios(String token, Short sucursal, String caja, String uuid, String amdSistema, String amdRequestId, String usuario, AmdSoapAutorizarPagoExecute requestSoap) {
        return (AmdSoapAutorizarPagoExecuteResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> {
            TransportContext context = TransportContextHolder.getTransportContext();
            HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
            ((SoapMessage) message).setSoapAction(Constantes.GLOBAL_SOAP_ACTION_PAGO_SERVICIOS);
            connection.addRequestHeader(Constantes.AUTH_TOKEN, token);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_SUBAGENCIA, String.valueOf(sucursal));
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_CAJA, caja);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_AMD_SISTEMA, amdSistema);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_AMD_REQUEST_ID, amdRequestId);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_USUARIO, usuario);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_UUID, uuid);
            connection.addRequestHeader(Constantes.AMD_TOKEN, token);
        });
    }

    public AmdSoapObtenerDatosXCuentaExecuteResponse informacion(String token, String uuid, String amdToken, AmdSoapObtenerDatosXCuentaExecute requestSoap) {
        return (AmdSoapObtenerDatosXCuentaExecuteResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> {
            TransportContext context = TransportContextHolder.getTransportContext();
            HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
            ((SoapMessage) message).setSoapAction(Constantes.GLOBAL_SOAP_ACTION_RECARGA);
            connection.addRequestHeader(Constantes.AUTH_TOKEN, token);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_UUID, uuid);
            connection.addRequestHeader(Constantes.AMD_TOKEN, token);
        });
    }

    public AmdSoapTransferirC2BancoExecuteResponse transferirSaldosC2B(String token, String uuid, String amdToken, String amdSistema, AmdSoapTransferirC2BancoExecute requestSoap) {
        return (AmdSoapTransferirC2BancoExecuteResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> {
            TransportContext context = TransportContextHolder.getTransportContext();
            HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
            ((SoapMessage) message).setSoapAction(Constantes.GLOBAL_SOAP_ACTION_TRANSFERENCIA_C2B);
            connection.addRequestHeader(Constantes.AUTH_TOKEN, token);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_AMD_REQUEST_ID, uuid);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_AMD_SISTEMA, amdSistema);
            connection.addRequestHeader(Constantes.AMD_TOKEN, token);
        });
    }

    public AmdSoapRecargarGPExecuteResponse recargaGP(String token, String uuid, String amdCliente, AmdSoapRecargarGPExecute requestSoap) {
        return (AmdSoapRecargarGPExecuteResponse) getWebServiceTemplate().marshalSendAndReceive(requestSoap, message -> {
            TransportContext context = TransportContextHolder.getTransportContext();
            HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
            ((SoapMessage) message).setSoapAction(Constantes.GLOBAL_SOAP_ACTION_RECARGA_GP);
            connection.addRequestHeader(Constantes.AUTH_TOKEN, token);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_AMD_REQUEST_ID, uuid);
            connection.addRequestHeader(Constantes.GLOBAL_HEADERS_AMD_CLIENTE, amdCliente);
            connection.addRequestHeader(Constantes.AMD_TOKEN, token);
        });
    }

}
