package uy.com.md.global.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.global.dto.CambioRequestDto;
import uy.com.md.global.soap.GlobalClient;
import uy.com.md.global.soap.client.aamdsoaprecargargp.AmdSoapRecargarGPExecute;
import uy.com.md.global.soap.client.aamdsoaprecargargp.AmdSoapRecargarGPExecuteResponse;
import uy.com.md.global.soap.client.aamdsoaprecargargp.SdtCamposValor;
import uy.com.md.global.soap.client.aamdsoaprecargargp.SdtCamposValorCampoValorItem;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeOrigen;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Origenes;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CambioService {

    @Autowired
    GlobalClient globalRecargaGPClient;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MensajeService mensajeService;

    public InternalResponseDto<AmdSoapRecargarGPExecuteResponse> cambio(String token, String uuid, CambioRequestDto cambioRequestDto) {
        InternalResponseDto<AmdSoapRecargarGPExecuteResponse> response = new InternalResponseDto<>();
        response.setResultado(Boolean.FALSE);
        mapDatosRequest(cambioRequestDto);
        AmdSoapRecargarGPExecuteResponse responseSoap = globalRecargaGPClient.recargaGP(token, uuid, cambioRequestDto.getAmdCliente(), mapDatosRequest(cambioRequestDto));
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


    private AmdSoapRecargarGPExecute mapDatosRequest(CambioRequestDto cambioRequestDto) {
        AmdSoapRecargarGPExecute amdSoapRecargarGPExecute = modelMapper.map(cambioRequestDto, AmdSoapRecargarGPExecute.class);
        if (cambioRequestDto.getSdtcamposvalor() != null && cambioRequestDto.getSdtcamposvalor().getSdtCamposValorCampoValorItem().size() > 0) {
            SdtCamposValor item = new SdtCamposValor();
            item.getSdtCamposValorCampoValorItem().addAll(mapList(cambioRequestDto.getSdtcamposvalor().getSdtCamposValorCampoValorItem(), SdtCamposValorCampoValorItem.class));
            amdSoapRecargarGPExecute.setSdtcamposvalor(item);
        }
        return amdSoapRecargarGPExecute;
    }

    private <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source
                .stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }
}
