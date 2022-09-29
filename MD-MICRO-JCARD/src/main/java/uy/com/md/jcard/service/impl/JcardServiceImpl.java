package uy.com.md.jcard.service.impl;

import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uy.com.md.common.util.Constantes;
import uy.com.md.common.util.SexoUtil;
import uy.com.md.jcard.client.CustomersClient;
import uy.com.md.jcard.client.JcardClient;
import uy.com.md.jcard.config.JcardRequestException;
import uy.com.md.jcard.dto.in.*;
import uy.com.md.jcard.dto.in.movientos_extendidos.MovimientoExtendidoDto;
import uy.com.md.jcard.dto.in.movimientos.JcardMovimientosParamsDto;
import uy.com.md.jcard.dto.in.movimientos.TransaccionesDto;
import uy.com.md.jcard.dto.in.movimientos.TransaccionesResponseDto;
import uy.com.md.jcard.dto.out.*;
import uy.com.md.jcard.service.JcardService;
import uy.com.md.jcard.service.MovimientosService;
import uy.com.md.jcard.service.SucursalService;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeOrigen;
import uy.com.md.mensajes.enumerated.Mensajes;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Origenes;

import java.util.Optional;

@Service
public class JcardServiceImpl implements JcardService {

    private static final Logger log = LoggerFactory.getLogger(JcardServiceImpl.class);

    @Autowired
    JcardClient jCardClient;

    @Autowired
    CustomersClient customersClient;

    @Autowired
    ModelMapper modelMapper;

    @Value("${jcard.version}")
    private String jcardVersion;

    @Value("${jcard.consumer.id}")
    private String consumerId;

    @Value("${jcard.canal}")
    private String jcardCanal;

    @Value("${jcard.motivo}")
    private String jcardMotivo;

    @Autowired
    MensajeService mensajeService;

    @Autowired
    MovimientosService movimientosService;

    @Autowired
    SucursalService sucursalService;

    @Override
    public InternalResponseDto<SolicitudResponseDto> getCards(String token, SolicitudRequestDto request) {
        InternalResponseDto<SolicitudResponseDto> response = new InternalResponseDto<>(null);
        response.setResultado(Boolean.FALSE);
        try {
            CardsRequestDto requestCard = modelMapper.map(request, CardsRequestDto.class);
            requestCard.getBeneficiario().setSexo(SexoUtil.mapToSexoJcard(requestCard.getBeneficiario().getSexo()));
            if (requestCard.getOperaPorTerceros() == null) {
                requestCard.setOperaPorTerceros(Boolean.FALSE);
            }
            requestCard.setSucursal(sucursalService.obtenerSucursal(request.getSucursal(), Long.parseLong(request.getRed()), Constantes.JCARD_NAME));
            CardsResponseDto cards = jCardClient.cards(token, jcardVersion, consumerId, requestCard);
            return getJCardsResponseDto(response, cards);
        } catch (JcardRequestException e) {
            response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.JCARD, e.getErrors(), e.getTrace())));
        } catch (Exception e) {
            log.error("getCards", e);
            response.addMensaje(mensajeService.obtenerMensajeDetail(Mensajes.MD_JCARD_ERROR));
        }
        return response;
    }

    @Override
    public InternalResponseDto<SolicitudResponseDto> cardDelivery(String token, EntregaRequestDto entregaRequestDto, String id) {
        InternalResponseDto<SolicitudResponseDto> response = new InternalResponseDto<>();
        response.setResultado(Boolean.FALSE);
        try {
            entregaRequestDto.setSucursal(sucursalService.obtenerSucursal(entregaRequestDto.getSucursal(), Long.parseLong(entregaRequestDto.getRed()), Constantes.JCARD_NAME));
            CardsResponseDto cards = jCardClient.cardDelivery(token, jcardVersion, consumerId, entregaRequestDto, id);
            return getJCardsResponseDto(response, cards);
        } catch (JcardRequestException je) {
            log.error("cardDelivery", je);
            response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.JCARD, je.getErrors(), je.getTrace())));
        } catch (Exception e) {
            log.error("cardDelivery", e);
            response.addMensaje(mensajeService.obtenerMensajeDetail(Mensajes.MD_JCARD_ERROR_CONSUMIENDO));
        }
        return response;
    }

    @Override
    public InternalResponseDto<SolicitudResponseDto> cardCredits(String token, RecargaRequestDto recargaRequestDto) {
        InternalResponseDto<SolicitudResponseDto> response = new InternalResponseDto<>();
        response.setResultado(Boolean.FALSE);
        try {
            recargaRequestDto.setCanal(jcardCanal);
            recargaRequestDto.setMotivo(jcardMotivo);
            recargaRequestDto.setSucursal(sucursalService.obtenerSucursal(recargaRequestDto.getSucursal(), Long.parseLong(recargaRequestDto.getRed()), Constantes.JCARD_NAME));
            CardsResponseDto cardsResponseDto = jCardClient.cardCredits(token, jcardVersion, consumerId, modelMapper.map(recargaRequestDto, RecargaRequestOutDto.class));
            return getJCardsResponseDto(response, cardsResponseDto);
        } catch (JcardRequestException je) {
            log.error("cardCredits", je);
            response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.JCARD, je.getErrors(), je.getTrace())));
        } catch (Exception e) {
            log.error("cardCredits", e);
            response.addMensaje(mensajeService.obtenerMensajeDetail(Mensajes.MD_JCARD_ERROR_CONSUMIENDO));
        }
        return response;
    }

    @Override
    @SneakyThrows
    public InternalResponseDto cardTransactions(String token, String cuenta, Optional<JcardMovimientosParamsDto> movimientosParamsDto) {
        InternalResponseDto response = new InternalResponseDto<>();
        response.setResultado(Boolean.FALSE);
        try {
            TransaccionesResponseDto transaccionesResponseDto;
            if (movimientosParamsDto.isPresent()) {
                transaccionesResponseDto = jCardClient.cardTransactions(token, jcardVersion, consumerId, cuenta, movimientosParamsDto.get());
            } else {
                transaccionesResponseDto = jCardClient.cardTransactions(token, jcardVersion, consumerId, cuenta, new JcardMovimientosParamsDto());
            }
            if (transaccionesResponseDto.getSuccess()) {
                response.setResultado(Boolean.TRUE);
                asignarInformacionExtendidaPorTransaccion(token, transaccionesResponseDto);
                response.setRespuesta(transaccionesResponseDto.getInfo());
            }
        } catch (JcardRequestException je) {
            log.error("cardTransactions", je);
            response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.JCARD, je.getErrors(), je.getTrace())));
        } catch (Exception e) {
            log.error("cardTransactions", e);
            response.addMensaje(mensajeService.obtenerMensajeDetail(Mensajes.MD_JCARD_ERROR));
        }
        return response;
    }

    @Override
    public InternalResponseDto<Boolean> cardTransfers(String token, TransferenciaRequestDto transferenciaRequestDto) {
        InternalResponseDto<Boolean> response = new InternalResponseDto<>(Boolean.FALSE, Boolean.FALSE);
        try {
            CardsResponseDto jcardResponseDto;
            jcardResponseDto = jCardClient.cardTransfersC2C(token, jcardVersion, consumerId, transferenciaRequestDto);
            if (jcardResponseDto.getSuccess()) {
                response.setResultado(Boolean.TRUE);
                response.setRespuesta(jcardResponseDto.getSuccess());
            }
        } catch (JcardRequestException je) {
            log.error("cardTransfers", je);
            response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.JCARD, je.getErrors(), je.getTrace())));
        } catch (Exception e) {
            log.error("cardTransfers", e);
            response.addMensaje(mensajeService.obtenerMensajeDetail(Mensajes.MD_JCARD_ERROR));
        }
        return response;
    }

    @Override
    public InternalResponseDto<SolicitudResponseDto> cardDebits(String token, PagoServiciosRequestDto pagoServiciosRequestDto) {
        InternalResponseDto<SolicitudResponseDto> response = new InternalResponseDto<>();
        response.setResultado(Boolean.FALSE);
        try {
            CardsResponseDto cardsResponseDto = jCardClient.cardDebits(token, jcardVersion, consumerId, modelMapper.map(pagoServiciosRequestDto, PagoServiciosRequestOutDto.class));
            return getJCardsResponseDto(response, cardsResponseDto);
        } catch (JcardRequestException je) {
            log.error("cardTransactions", je);
            response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.JCARD, je.getErrors(), je.getTrace())));
        } catch (Exception e) {
            log.error("cardTransactions", e);
            response.addMensaje(mensajeService.obtenerMensajeDetail(Mensajes.MD_JCARD_ERROR));
        }
        return response;
    }

    @Override
    public InternalResponseDto customerInfo(String token, String cuenta) {
        InternalResponseDto response = new InternalResponseDto();
        response.setResultado(Boolean.FALSE);
        try {
            response.setRespuesta(customersClient.customerInfo(token, jcardVersion, consumerId, cuenta).getInfo());
            response.setResultado(Boolean.TRUE);
        } catch (JcardRequestException je) {
            log.error("customerInfo", je);
            response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.JCARD, je.getErrors(), je.getTrace())));
        } catch (Exception e) {
            log.error("customerInfo", e);
            response.addMensaje(mensajeService.obtenerMensajeDetail(Mensajes.MD_JCARD_ERROR));
        }
        return response;
    }

    private InternalResponseDto<SolicitudResponseDto> getJCardsResponseDto(InternalResponseDto<SolicitudResponseDto> response, CardsResponseDto cards) {
        if (cards != null) {
            SolicitudResponseDto responseJ = modelMapper.map(cards, SolicitudResponseDto.class);
            response.setRespuesta(responseJ);
            response.setResultado(Boolean.TRUE);
            if (responseJ.getErrors() != null && !responseJ.getErrors().isEmpty()) {
                response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.JCARD, responseJ.getErrors(), responseJ.getTrace())));
                response.setResultado(Boolean.FALSE);
            }
        } else {
            response.addMensaje(mensajeService.obtenerMensajeDetail(Mensajes.MD_JCARD_ERROR));
        }
        return response;
    }

    private void asignarInformacionExtendidaPorTransaccion(String token, TransaccionesResponseDto transaccionesResponseDto) {
        for (TransaccionesDto transaccionesDto : transaccionesResponseDto.getInfo().getTransactions()) {
            transaccionesDto.setInformacionExtendida((obtenerIformacionExtendidaPorTransaccion(token, transaccionesDto) != null) ? obtenerIformacionExtendidaPorTransaccion(token, transaccionesDto).getInfoExtendida() : null);
        }
    }

    private MovimientoExtendidoDto obtenerIformacionExtendidaPorTransaccion(String token, TransaccionesDto transaccionesDto) {
        return movimientosService.obtenerMovimientoExtendido(token, transaccionesDto.getRrn()).getRespuesta();
    }
}
