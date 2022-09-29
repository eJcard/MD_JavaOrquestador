package uy.com.md.global.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.global.dto.EntregaRequestDto;
import uy.com.md.global.soap.GlobalClient;
import uy.com.md.global.soap.client.aamdsoapentreganominada.AmdSoapEntregaNominadaExecute;
import uy.com.md.global.soap.client.aamdsoapentreganominada.AmdSoapEntregaNominadaExecuteResponse;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeOrigen;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Origenes;

@Service
public class EntregaService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    GlobalClient globalEntregaClient;

    @Autowired
    MensajeService mensajeService;

   public InternalResponseDto<AmdSoapEntregaNominadaExecuteResponse> entrega(String token, String uuid, EntregaRequestDto entregaNominadaRequestDto) {
        InternalResponseDto<AmdSoapEntregaNominadaExecuteResponse> response = new InternalResponseDto<>();
        response.setResultado(Boolean.FALSE);

            AmdSoapEntregaNominadaExecute entregaNominadaRequestSoap = modelMapper.map(entregaNominadaRequestDto, AmdSoapEntregaNominadaExecute.class);
            AmdSoapEntregaNominadaExecuteResponse responseSoap = globalEntregaClient.entrega(token, entregaNominadaRequestDto.getSucursal(), entregaNominadaRequestDto.getCaja(), uuid, entregaNominadaRequestSoap);
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
}
