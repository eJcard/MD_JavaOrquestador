package uy.com.md.global.service;

import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.global.dto.TransferenciaRequestDto;
import uy.com.md.global.soap.GlobalClient;
import uy.com.md.global.soap.client.aamdsoaptransferirc2c.AmdSoapTransferirC2CExecute;
import uy.com.md.global.soap.client.aamdsoaptransferirc2c.AmdSoapTransferirC2CExecuteResponse;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeOrigen;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Origenes;

import javax.xml.datatype.DatatypeConfigurationException;

@Service
public class TransferenciaC2CService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    GlobalClient globalTransferenciaC2CClient;

    @Autowired
    MensajeService mensajeService;

    @SneakyThrows
    public InternalResponseDto<AmdSoapTransferirC2CExecuteResponse> transferirSaldosC2C(String token, String uuid, TransferenciaRequestDto transferenciaC2CRequestDto) {
        InternalResponseDto<AmdSoapTransferirC2CExecuteResponse> response = new InternalResponseDto<>();
        response.setResultado(Boolean.FALSE);

        AmdSoapTransferirC2CExecute requestSoap = modelMapper.map(transferenciaC2CRequestDto, AmdSoapTransferirC2CExecute.class);
        mapDatosRequest(requestSoap, transferenciaC2CRequestDto);
        AmdSoapTransferirC2CExecuteResponse responseSoap = globalTransferenciaC2CClient.transferirSaldosC2C(token, uuid, transferenciaC2CRequestDto.getAmdToken(), transferenciaC2CRequestDto.getAmdSistema(), requestSoap);
        response.setRespuesta(responseSoap);
        response.setResultado(Boolean.TRUE);
        if (responseSoap.getResponse().getResultadoEstado() != 200) {
            response.setRespuesta(null);
            response.setResultado(Boolean.FALSE);
        }
        //TODO: Check propagacion de error desde backend
        if (responseSoap != null && responseSoap.getResponse() != null && responseSoap.getResponse().getResultadoMensaje() != null && !responseSoap.getResponse().getResultadoMensaje().isEmpty()) {
            response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.GLOBAL, responseSoap.getResponse().getResultadoMensaje(), responseSoap.getResponse().getResultadoMensaje())));
            response.setResultado(Boolean.FALSE);
        }
        return response;
    }

    private AmdSoapTransferirC2CExecute mapDatosRequest(AmdSoapTransferirC2CExecute requestSoap, TransferenciaRequestDto transferenciaC2CRequestDto) throws DatatypeConfigurationException, NumberFormatException {
        Long cuentaOrigen = Long.parseLong(transferenciaC2CRequestDto.getOrdenante().getCuenta());
        Long cuentaDestino = Long.parseLong(transferenciaC2CRequestDto.getBeneficiario().getCuenta());
        requestSoap.setCuentaorigen(cuentaOrigen);
        requestSoap.setCuentadestino(cuentaDestino);
        requestSoap.setMoneda(transferenciaC2CRequestDto.getMoneda().shortValue());
        requestSoap.setMonto(transferenciaC2CRequestDto.getMonto().doubleValue());
        requestSoap.setConcepto(transferenciaC2CRequestDto.getMotivo());
        return requestSoap;
    }

}
