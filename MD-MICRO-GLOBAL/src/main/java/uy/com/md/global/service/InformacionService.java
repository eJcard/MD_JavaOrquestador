package uy.com.md.global.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.global.dto.CuentaDto;
import uy.com.md.global.soap.GlobalClient;
import uy.com.md.global.soap.client.aamdsoapobtenerdatosxcuenta.AmdSoapObtenerDatosXCuentaExecute;
import uy.com.md.global.soap.client.aamdsoapobtenerdatosxcuenta.AmdSoapObtenerDatosXCuentaExecuteResponse;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeOrigen;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Origenes;

@Service
public class InformacionService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    GlobalClient globalInformacionClient;

    @Autowired
    MensajeService mensajeService;

    public InternalResponseDto<AmdSoapObtenerDatosXCuentaExecuteResponse> informacionCuenta(String token, String uuid, CuentaDto cuentaDto) {
        InternalResponseDto<AmdSoapObtenerDatosXCuentaExecuteResponse> response = new InternalResponseDto<>();
        response.setResultado(Boolean.FALSE);

            AmdSoapObtenerDatosXCuentaExecuteResponse responseSoap = globalInformacionClient.informacion(token, uuid, cuentaDto.getAmdToken(), modelMapper.map(cuentaDto, AmdSoapObtenerDatosXCuentaExecute.class));
            response.setRespuesta(responseSoap);
            response.setResultado(Boolean.TRUE);
            if (responseSoap.getSdtresultadoestado().getResultadoEstado() != 200) {
                response.setRespuesta(null);
                response.setResultado(Boolean.FALSE);
            }
            if (responseSoap != null && responseSoap.getSdtresultadoestado() != null && responseSoap.getSdtresultadoestado().getResultadoMensaje() != null && !responseSoap.getSdtresultadoestado().getResultadoMensaje().isEmpty() && !responseSoap.getSdtresultadoestado().getResultadoMensaje().equalsIgnoreCase("OK")) {
                response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.GLOBAL, responseSoap.getSdtresultadoestado().getResultadoMensaje(), responseSoap.getSdtresultadoestado().getResultadoMensaje())));
                response.setResultado(Boolean.FALSE);
            }

        return response;
    }
}
