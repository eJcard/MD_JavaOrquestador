package uy.com.md.global.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.global.dto.RecargaRequestDto;
import uy.com.md.global.soap.GlobalClient;
import uy.com.md.global.soap.client.aamdsoaprecargartarjeta.AmdSoapRecargarTarjetaExecute;
import uy.com.md.global.soap.client.aamdsoaprecargartarjeta.AmdSoapRecargarTarjetaExecuteResponse;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeOrigen;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Origenes;


@Service
public class RecargaService {


    @Autowired
    GlobalClient globalRecargaClient;

    @Autowired
    MensajeService mensajeService;

    public InternalResponseDto<AmdSoapRecargarTarjetaExecuteResponse> recarga(String token, String uuid, RecargaRequestDto recargaRequestDto) {
        InternalResponseDto<AmdSoapRecargarTarjetaExecuteResponse> response = new InternalResponseDto<>();
        response.setResultado(Boolean.FALSE);

            AmdSoapRecargarTarjetaExecuteResponse responseSoap = globalRecargaClient.recarga(token, recargaRequestDto.getSucursal(), recargaRequestDto.getCaja(), uuid, mapDatosRequest(recargaRequestDto));
            response.setRespuesta(responseSoap);
            response.setResultado(Boolean.TRUE);
            if (responseSoap.getResponse().getResultadoEstado() != 200) {
                response.setRespuesta(null);
                response.setResultado(Boolean.FALSE);
            }
            if (responseSoap != null && responseSoap.getResponse() != null && responseSoap.getResponse().getResultadoMensaje() != null && !responseSoap.getResponse().getResultadoMensaje().isEmpty()) {
                response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.GLOBAL, responseSoap.getResponse().getResultadoMensaje(), responseSoap.getResponse().getResultadoMensaje())));
                response.setResultado(Boolean.FALSE);
            }

        return response;
    }

    private AmdSoapRecargarTarjetaExecute mapDatosRequest(RecargaRequestDto recargaRequestDto) {
        AmdSoapRecargarTarjetaExecute requestSoap = new AmdSoapRecargarTarjetaExecute();
        requestSoap.setPaisdocumento(recargaRequestDto.getBeneficiario().getPais());
        requestSoap.setTipodocumento(recargaRequestDto.getBeneficiario().getTipoDoc());
        requestSoap.setDocumento(recargaRequestDto.getBeneficiario().getDoc());
        requestSoap.setTipoproducto(recargaRequestDto.getProducto());
        requestSoap.setMoneda(recargaRequestDto.getMoneda());
        requestSoap.setImporte(recargaRequestDto.getMonto());
        requestSoap.setReferencia(recargaRequestDto.getComprobante());
        return requestSoap;
    }

}
