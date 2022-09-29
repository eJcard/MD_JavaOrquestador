package uy.com.md.global.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.common.util.Constantes;
import uy.com.md.common.util.MonedaUtil;
import uy.com.md.global.dto.TransferenciaRequestDto;
import uy.com.md.global.soap.GlobalClient;
import uy.com.md.global.soap.client.aamdsoaptransferirc2banco.AmdSoapTransferirC2BancoExecute;
import uy.com.md.global.soap.client.aamdsoaptransferirc2banco.AmdSoapTransferirC2BancoExecuteResponse;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeOrigen;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Origenes;

@Service
public class TransferenciaC2BService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    GlobalClient globalTransferenciaC2BClient;

    @Autowired
    MensajeService mensajeService;


    public InternalResponseDto<AmdSoapTransferirC2BancoExecuteResponse> transferirSaldosC2B(String token, String uuid, TransferenciaRequestDto transferenciaC2CRequestDto) {
        InternalResponseDto<AmdSoapTransferirC2BancoExecuteResponse> response = new InternalResponseDto<>();
        response.setResultado(Boolean.FALSE);
        AmdSoapTransferirC2BancoExecute requestSoap = modelMapper.map(transferenciaC2CRequestDto, AmdSoapTransferirC2BancoExecute.class);
        mapDatosRequest(requestSoap, transferenciaC2CRequestDto);
        AmdSoapTransferirC2BancoExecuteResponse responseSoap = globalTransferenciaC2BClient.transferirSaldosC2B(token, uuid, transferenciaC2CRequestDto.getAmdToken(), transferenciaC2CRequestDto.getAmdSistema(), requestSoap);
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

    private AmdSoapTransferirC2BancoExecute mapDatosRequest(AmdSoapTransferirC2BancoExecute requestSoap, TransferenciaRequestDto transferenciaRequestDto) {
        requestSoap.setCuentaorigen(Long.parseLong(transferenciaRequestDto.getOrdenante().getCuenta()));
        requestSoap.setBancodestino(Short.parseShort(transferenciaRequestDto.getBeneficiario().getBanco().getId()));
        requestSoap.setSucursalbancodestino(Short.parseShort(transferenciaRequestDto.getBeneficiario().getBanco().getSucursal()));
        requestSoap.setCuentabancodestino(transferenciaRequestDto.getBeneficiario().getBanco().getCuenta());
        requestSoap.setNombrepersonabancodestino(transferenciaRequestDto.getBeneficiario().getNombre());
        requestSoap.setMonto(transferenciaRequestDto.getMonto().doubleValue());
        requestSoap.setMoneda(MonedaUtil.converterMonedaParaProcesador(Constantes.GLOBAL_NAME, transferenciaRequestDto.getMoneda().toString()));
        requestSoap.setConcepto(transferenciaRequestDto.getConcepto());
        requestSoap.setReferencia(transferenciaRequestDto.getReferencia());
        return requestSoap;
    }
}
