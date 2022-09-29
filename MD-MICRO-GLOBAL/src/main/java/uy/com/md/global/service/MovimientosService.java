package uy.com.md.global.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.global.dto.MovimientosRequestDto;
import uy.com.md.global.soap.GlobalClient;
import uy.com.md.global.soap.client.aamdsoapobtenermismovimientos.AmdSoapObtenerMisMovimientosV2Execute;
import uy.com.md.global.soap.client.aamdsoapobtenermismovimientos.AmdSoapObtenerMisMovimientosV2ExecuteResponse;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeOrigen;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Origenes;

@Service
public class MovimientosService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    GlobalClient globalMovimientosClient;

    @Autowired
    MensajeService mensajeService;


    public InternalResponseDto<AmdSoapObtenerMisMovimientosV2ExecuteResponse> movimientos(String token, String uuid, MovimientosRequestDto movimientosRequestDto) {
        InternalResponseDto<AmdSoapObtenerMisMovimientosV2ExecuteResponse> response = new InternalResponseDto<>();
        response.setResultado(Boolean.FALSE);

            AmdSoapObtenerMisMovimientosV2ExecuteResponse responseSoap = globalMovimientosClient.movimientos(token, movimientosRequestDto.getSucursal(), movimientosRequestDto.getCaja(), uuid, modelMapper.map(movimientosRequestDto, AmdSoapObtenerMisMovimientosV2Execute.class));
            response.setRespuesta(responseSoap);
            response.setResultado(Boolean.TRUE);
            if (responseSoap.getResponse().getResultadoEstado() != 200) {
                response.setRespuesta(null);
                response.setResultado(Boolean.FALSE);
            }
            if (responseSoap != null && responseSoap.getResponse() != null && responseSoap.getResponse().getResultadoMensaje() != null && !responseSoap.getResponse().getResultadoMensaje().isEmpty() && !responseSoap.getResponse().getResultadoMensaje().equals("OK")) {
                response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.GLOBAL, responseSoap.getResponse().getResultadoMensaje(), responseSoap.getResponse().getResultadoMensaje())));
                response.setResultado(Boolean.FALSE);
            }

        return response;
    }
}
