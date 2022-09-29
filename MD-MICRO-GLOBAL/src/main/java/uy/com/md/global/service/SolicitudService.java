package uy.com.md.global.service;

import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.common.util.DateUtil;
import uy.com.md.global.dto.*;
import uy.com.md.global.soap.GlobalClient;
import uy.com.md.global.soap.client.solicitudproductonominado.AmdSoapSolicitudProductoNominadoV2Execute;
import uy.com.md.global.soap.client.solicitudproductonominado.AmdSoapSolicitudProductoNominadoV2ExecuteResponse;
import uy.com.md.global.soap.client.solicitudproductonominado.SdtDatosPersonaV2;
import uy.com.md.global.soap.client.solicitudproductonominado.SdtDatosRepresentante;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeOrigen;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Origenes;

import javax.xml.datatype.DatatypeConfigurationException;

@Service
public class SolicitudService {

    private static Logger log = LoggerFactory.getLogger(SolicitudService.class);

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    GlobalClient globalSolicitudClient;

    @Autowired
    MensajeService mensajeService;

    @SneakyThrows
    public InternalResponseDto<AmdSoapSolicitudProductoNominadoV2ExecuteResponse> solicitud(String token, String uuid, SolcitudRequestDto solcitudRequestDto) {
        InternalResponseDto<AmdSoapSolicitudProductoNominadoV2ExecuteResponse> response = new InternalResponseDto<>();
        response.setResultado(Boolean.FALSE);

        AmdSoapSolicitudProductoNominadoV2Execute requestSoap = modelMapper.map(solcitudRequestDto, AmdSoapSolicitudProductoNominadoV2Execute.class);
        mapDatosRequest(requestSoap, solcitudRequestDto);
        AmdSoapSolicitudProductoNominadoV2ExecuteResponse responseSoap = globalSolicitudClient.solicitud(token, solcitudRequestDto.getSucursalOrigen(), solcitudRequestDto.getCaja(), uuid, requestSoap);
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

    private AmdSoapSolicitudProductoNominadoV2Execute mapDatosRequest(AmdSoapSolicitudProductoNominadoV2Execute requestSoap, SolcitudRequestDto solcitudRequestDto) throws DatatypeConfigurationException {
        PersonaDto beneficiario = solcitudRequestDto.getBeneficiario();

        SdtDatosPersonaV2 sdtDatosPersona = modelMapper.map(beneficiario, SdtDatosPersonaV2.class);

        mapDatosPersona(sdtDatosPersona, beneficiario);
        mapDireccionPersona(sdtDatosPersona, beneficiario);
        mapDocumentoPersona(sdtDatosPersona, beneficiario);
        mapTelefonoPersona(sdtDatosPersona, beneficiario);

        requestSoap.setDatospersona(sdtDatosPersona);

        PersonaDto representante = solcitudRequestDto.getRepresentante();
        if (representante != null) {
            SdtDatosRepresentante datosrepresentante = modelMapper.map(representante, SdtDatosRepresentante.class);

            mapDatosPersona(datosrepresentante, representante);
            mapDireccionPersona(datosrepresentante, representante);
            mapDocumentoPersona(datosrepresentante, representante);
            mapTelefonoPersona(datosrepresentante, representante);
            mapRelacionRepresentante(datosrepresentante, representante);

            requestSoap.setDatosrepresentante(datosrepresentante);
        }

        mapSucursalaRetiro(requestSoap, solcitudRequestDto);

        return requestSoap;
    }

    private SdtDatosPersonaV2 mapDatosPersona(SdtDatosPersonaV2 sdtDatosPersona, PersonaDto personaDto) throws DatatypeConfigurationException {
        sdtDatosPersona.setPrimerNombre(personaDto.getNombre());
        sdtDatosPersona.setSegundoNombre(personaDto.getNombre2());
        sdtDatosPersona.setPrimerApellido(personaDto.getApellido());
        sdtDatosPersona.setSegundoApellido(personaDto.getApellido2());
        sdtDatosPersona.setPaisNacimiento(personaDto.getNacionalidad());
        sdtDatosPersona.setFechaNacimiento(DateUtil.convertStringToXMLGregorianCalendar(personaDto.getFechaNacimiento()));
        return sdtDatosPersona;
    }

    private SdtDatosPersonaV2 mapDireccionPersona(SdtDatosPersonaV2 sdtDatosPersona, PersonaDto personaDto) {
        DireccionDto direccionDto = personaDto.getDireccion();
        sdtDatosPersona.setDireccionResidente(direccionDto.getResidente());
        sdtDatosPersona.setCalle(direccionDto.getCalle());
        sdtDatosPersona.setPuerta(direccionDto.getPuerta());
        sdtDatosPersona.setDireccionCodigoPostal(direccionDto.getCodigoPostal());
        if (direccionDto.getDepartamento() != null) {
            sdtDatosPersona.setDepartamento(direccionDto.getDepartamento());
        }
        if (direccionDto.getDepartamento() != null) {
            sdtDatosPersona.setLocalidad(direccionDto.getLocalidad());
        }
        String ampliacionCallePersona = direccionDto.getAmpliacionCalle();
        if (ampliacionCallePersona != null) {
            sdtDatosPersona.setDireccionDetalle(ampliacionCallePersona);
        }
        sdtDatosPersona.setPaisDocumento(direccionDto.getPais());
        sdtDatosPersona.setDireccionPiso(direccionDto.getPiso());
        sdtDatosPersona.setApartamento(direccionDto.getApto());
        if (direccionDto.getBarrio() != null) {
            sdtDatosPersona.setBarrio(direccionDto.getBarrio());
        }
        if (direccionDto.getAmpliacionCalle() != null) {
            sdtDatosPersona.setDireccionDetalle(direccionDto.getAmpliacionCalle());
        }
        return sdtDatosPersona;
    }

    private SdtDatosPersonaV2 mapDocumentoPersona(SdtDatosPersonaV2 sdtDatosPersona, PersonaDto personaDto) {
        DocumentoDto documentoDto = personaDto.getDoc();
        sdtDatosPersona.setTipoDocumento(documentoDto.getTipoDoc());
        sdtDatosPersona.setNumeroDocumento(documentoDto.getNum());
        return sdtDatosPersona;
    }

    private SdtDatosPersonaV2 mapTelefonoPersona(SdtDatosPersonaV2 sdtDatosPersona, PersonaDto personaDto) {
        TelefonoDto[] telefonoDto = personaDto.getTelefonos();
        if (telefonoDto.length > 0) {
            if (telefonoDto.length > 1) {
                sdtDatosPersona.setDireccionTelefAlternativo(telefonoDto[1].getNum());
            }
            sdtDatosPersona.setDireccionTelefono(telefonoDto[0].getNum());
        }
        return sdtDatosPersona;
    }

    private SdtDatosRepresentante mapRelacionRepresentante(SdtDatosRepresentante datosrepresentante, PersonaDto personaDto) {
        datosrepresentante.setTipoRelacion(personaDto.getRelacion());
        return datosrepresentante;
    }

    private AmdSoapSolicitudProductoNominadoV2Execute mapSucursalaRetiro(AmdSoapSolicitudProductoNominadoV2Execute requestSoap, SolcitudRequestDto solcitudRequestDto) {
        requestSoap.setSubagenciaretiro(solcitudRequestDto.getSucursal());
        return requestSoap;
    }
}
