package uy.com.md.global.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.global.dto.AutorizarPagoRequestDto;
import uy.com.md.global.soap.GlobalClient;
import uy.com.md.global.soap.client.aamdsoapautorizarpago.AmdAutorizarPagoEntrada;
import uy.com.md.global.soap.client.aamdsoapautorizarpago.AmdSoapAutorizarPagoExecute;
import uy.com.md.global.soap.client.aamdsoapautorizarpago.AmdSoapAutorizarPagoExecuteResponse;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeOrigen;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Origenes;

@Service
public class PagoServiciosService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    GlobalClient globalPagoServiciosClient;

    @Autowired
    MensajeService mensajeService;

    public InternalResponseDto<AmdSoapAutorizarPagoExecuteResponse> pagoServicios(String token, String uuid, AutorizarPagoRequestDto autorizarPagoRequestDto) {
        InternalResponseDto<AmdSoapAutorizarPagoExecuteResponse> response = new InternalResponseDto<>();
        response.setResultado(Boolean.FALSE);

            AmdAutorizarPagoEntrada autorizarPagoEntrada = modelMapper.map(autorizarPagoRequestDto, AmdAutorizarPagoEntrada.class);
            AmdSoapAutorizarPagoExecute entregaNominadaRequestSoap = new AmdSoapAutorizarPagoExecute();
            entregaNominadaRequestSoap.setEntrada(autorizarPagoEntrada);
            AmdSoapAutorizarPagoExecuteResponse responseSoap = globalPagoServiciosClient.pagoServicios(token, autorizarPagoRequestDto.getSucursal(), autorizarPagoRequestDto.getCaja(), uuid, autorizarPagoRequestDto.getAmdSistema(), uuid, autorizarPagoRequestDto.getUsuario(), entregaNominadaRequestSoap);
            response.setRespuesta(responseSoap);
            response.setResultado(Boolean.TRUE);
            if (responseSoap.getResponse().getResultadoEstado() != 200) {
                response.setRespuesta(null);
                response.setResultado(Boolean.FALSE);
            }
            if (responseSoap != null && responseSoap.getResponse() != null && responseSoap.getResponse().getResultadoMensaje() != null && !responseSoap.getResponse().getResultadoMensaje().isEmpty() && !responseSoap.getResponse().getResultadoMensaje().equalsIgnoreCase("OK")) {
                response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.GLOBAL, responseSoap.getResponse().getResultadoMensaje(), responseSoap.getResponse().getResultadoMensaje())));
                response.setResultado(Boolean.FALSE);
            }

        return response;
    }
}
