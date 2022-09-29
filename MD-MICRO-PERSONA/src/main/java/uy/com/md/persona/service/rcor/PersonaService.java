package uy.com.md.persona.service.rcor;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.enumerated.Mensajes;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.persona.dto.rcor.PersonaDto;
import uy.com.md.persona.dto.rcor.PersonaFisicaDto;
import uy.com.md.persona.dto.rcor.PersonaJuridicaDto;
import uy.com.md.persona.model.rcor.*;
import uy.com.md.persona.repository.rcor.*;
import uy.com.md.persona.util.Constantes;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class PersonaService {

    private static final Logger log = LoggerFactory.getLogger(PersonaService.class);

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PersonaFisicaService personaFisicaService;

    @Autowired
    ContactoService contactoService;

    @Autowired
    PersonaJuridicaService personaJuridicaService;

    @Autowired
    VinculoService vinculoService;

    @Autowired
    MensajeService mensajeService;

    @Autowired
    PersonaRepository personaRepository;

    @Autowired
    PersonaFisicaRepository personaFisicaRepository;

    @Autowired
    PersonaJuridicaRepository personaJuridicaRepository;

    @Autowired
    ContactoRepository contactoRepository;

    @Autowired
    DireccionRepository direccionRepository;

    public InternalResponseDto<PersonaDto> crearPersona(String origen, PersonaDto personaDto) {
        InternalResponseDto<PersonaDto> response = new InternalResponseDto<>();
        PersonaDto persona = obtener(origen, personaDto.getCodPaisDocId(), personaDto.getCodTipoDocId(), personaDto.getNumDoc());
        Persona personaEntity;
        if (persona == null) {
            personaEntity = personaRepository.saveAndFlush(modelMapper.map(personaDto, Persona.class));
        } else {
            personaEntity = modelMapper.map(personaDto, Persona.class);
            personaEntity.setId(persona.getId());
            personaEntity.setFcreacion(LocalDateTime.now());
            personaEntity = personaRepository.save(personaEntity);
        }
        persona = modelMapper.map(personaEntity, PersonaDto.class);
        if (persona.getId() != null) {
            if (personaDto.getPersonaFisicaDto() != null) {
                persona.setPersonaFisicaDto(crearPersonaFisica(origen, persona.getId(), personaDto));
            }
            if (personaDto.getPersonaJuridicaDto() != null) {
                persona.setPersonaJuridicaDto(crearPersonaJuridica(origen, persona.getId(), personaDto));
            }
            if (personaDto.getContactos() != null) {
                persona.setContactos(contactoService.persistContactos(origen, persona, personaDto.getContactos()));
            }
            response.setResultado(Boolean.TRUE);
            eliminarOrigen(persona);
            response.setRespuesta(persona);
        } else {
            eliminar(origen, persona.getId());
            response.setResultado(Boolean.FALSE);
            response.setRespuesta(null);
            response.addMensaje(mensajeService.obtenerMensajeDetail(Mensajes.MD_PERSONA_ERROR));
        }
        return response;
    }

    public PersonaDto obtener(String origen, Integer personaId) {
        Optional<Persona> obj = personaRepository.findById(personaId);
        if (obj.isPresent()) {
            PersonaDto dto = buildDtoWithEntity(origen, obj);
            return eliminarOrigen(dto);
        } else {
            return null;
        }
    }

    public PersonaDto obtener(String origen, Integer codPaisDocId, Integer codTipoDocId, String numDoc) {
        Optional<Persona> obj = personaRepository.obtPersonaPorDocumento(codPaisDocId, codTipoDocId, numDoc);
        if (obj.isPresent()) {
            PersonaDto dto = buildDtoWithEntity(origen, obj);
            return eliminarOrigen(dto);
        } else {
            return null;
        }
    }

    public Boolean eliminar(String origen, Integer personaId) {
        String origenUp = origen.toUpperCase();
        boolean eliminado = Boolean.FALSE;
        if (origenUp.equals(Constantes.ORIGEN_DIGITAL)) {
            eliminado = vinculoService.eliminarVinculo(personaId);
            List<Contacto> contactos = contactoRepository.findByPersonaIdAndOrigenId(personaId, origenUp);
            for (Contacto contacto : contactos) {
                try {
                    contactoRepository.delete(contacto);
                    eliminado = Boolean.TRUE;
                } catch (Exception ex) {
                    eliminado = Boolean.FALSE;
                    log.error("Error eliminando contactos de la persona con id: " + personaId, ex);
                }
            }
            Optional<Direccion> direccion = direccionRepository.findByPersonaIdAndOrigenId(personaId, origenUp);
            if (direccion.isPresent()) {
                try {
                    direccionRepository.delete(direccion.get());
                    eliminado = Boolean.TRUE;
                } catch (Exception ex) {
                    eliminado = Boolean.FALSE;
                    log.error("Error eliminando la persona física con id: " + personaId, ex);
                }
            }
            Optional<PersonaFisica> personaFisica = personaFisicaRepository.findByPersonaIdAndOrigenId(personaId, origenUp);
            if (personaFisica.isPresent()) {
                try {
                    personaFisicaRepository.delete(personaFisica.get());
                    eliminado = Boolean.TRUE;
                } catch (Exception ex) {
                    eliminado = Boolean.FALSE;
                    log.error("Error eliminando la persona física con id: " + personaId, ex);
                }
            }
            Optional<PersonaJuridica> personaJuridica = personaJuridicaRepository.findByPersonaIdAndOrigenId(personaId, origenUp);
            if (personaJuridica.isPresent()) {
                try {
                    personaJuridicaRepository.delete(personaJuridica.get());
                    eliminado = Boolean.TRUE;
                } catch (Exception ex) {
                    eliminado = Boolean.FALSE;
                    log.error("Error eliminando la persona jurídica con id: " + personaId, ex);
                }
            }
        }
        return eliminado;

    }

    private PersonaFisicaDto crearPersonaFisica(String origen, Integer personaId, PersonaDto personaDto) {
        personaDto.getPersonaFisicaDto().setPersonaId(personaId);
        personaDto.getPersonaFisicaDto().setUact(personaDto.getUact());
        personaDto.getPersonaFisicaDto().setCorrId(personaDto.getCorrId());
        return personaFisicaService.createPersonaFisica(origen, personaDto.getPersonaFisicaDto());
    }

    private PersonaJuridicaDto crearPersonaJuridica(String origen, Integer personaId, PersonaDto personaDto) {
        personaDto.getPersonaJuridicaDto().setPersonaId(personaId);
        personaDto.getPersonaJuridicaDto().setUact(personaDto.getUact());
        personaDto.getPersonaJuridicaDto().setCorrId(personaDto.getCorrId());
        return personaJuridicaService.createPersonaJuridica(origen, personaDto.getPersonaJuridicaDto());
    }


    private PersonaDto buildDtoWithEntity(String origen, Optional<Persona> obj) {
        PersonaDto dto = new PersonaDto();
        dto.setCodPaisDocId(obj.get().getCodPaisDocId());
        dto.setCodTipoDocId(obj.get().getCodTipoDocId());
        dto.setCodTipoPersonaId(obj.get().getCodTipoPersonaId());
        dto.setDenominacion(obj.get().getDenominacion());
        dto.setFact(obj.get().getFact());
        dto.setFcreacion(obj.get().getFcreacion());
        dto.setId(obj.get().getId());
        dto.setNumDoc(obj.get().getNumDoc());
        dto.setPersonaActiva(obj.get().getPersonaActiva());
        dto.setUact(obj.get().getUact());
        dto.setUcreacion(obj.get().getUcreacion());
        if (obj.get().getCodTipoPersonaId().equals(Constantes.COD_TIPO_PERSONA_FISICA)) {
            dto.setPersonaFisicaDto(personaFisicaService.obtener(origen, obj.get().getId()));
        } else {
            dto.setPersonaJuridicaDto(personaJuridicaService.obtener(origen, obj.get().getId()));
        }
        dto.setContactos(contactoService.obtenerListaContactosPersona(obj.get().getId(), origen));
        return dto;
    }

    public void actualizarPersona(String origen, Integer personaId, PersonaDto request) {
        Optional<Persona> entity = personaRepository.findById(personaId);
        if (entity.isPresent()) {
            entity.get().setFact(LocalDateTime.now());
            entity.get().setUact(request.getUact());
            if (request.getCodTipoDocId() != null && (entity.get().getCodTipoDocId() == null || entity.get().getCodTipoDocId() != request.getCodTipoDocId())) {
                entity.get().setCodTipoDocId(request.getCodTipoDocId());
            }
            if (request.getPersonaActiva() != null && (entity.get().getPersonaActiva() == null || entity.get().getPersonaActiva() != request.getPersonaActiva())) {
                entity.get().setPersonaActiva(request.getPersonaActiva());
            }
            if (request.getDenominacion() != null && (entity.get().getDenominacion() == null || !entity.get().getDenominacion().equals(request.getDenominacion()))) {
                entity.get().setDenominacion(request.getDenominacion());
            }
            if (request.getNumDoc() != null && (entity.get().getNumDoc() == null || !entity.get().getNumDoc().equals(request.getNumDoc()))) {
                entity.get().setNumDoc(request.getNumDoc());
            }
            personaRepository.saveAndFlush(entity.get());
            if (request.getPersonaFisicaDto() != null)
                personaFisicaService.actualizarPersonaFisica(origen, personaId, request.getPersonaFisicaDto(), request.getUact());
            if (request.getPersonaJuridicaDto() != null)
                personaJuridicaService.actualizarPersonaJuridica(origen, personaId, request.getPersonaJuridicaDto(), request.getUact());
            if (request.getContactos() != null)
                contactoService.actualizarContactosPersona(personaId, request.getContactos(), request.getUact(), origen);
        }
    }

    private PersonaDto eliminarOrigen(PersonaDto personaDto) {
        if (personaDto.getPersonaFisicaDto() != null) {
            personaDto.getPersonaFisicaDto().setOrigenId(null);
            if (personaDto.getPersonaFisicaDto().getDireccion() != null) {
                personaDto.getPersonaFisicaDto().getDireccion().setOrigenId(null);
            }
            personaDto.getContactos().forEach(contactoDto -> contactoDto.setOrigen(null));
        }
        if (personaDto.getPersonaJuridicaDto() != null) {
            personaDto.getPersonaJuridicaDto().setOrigenId(null);
            if (personaDto.getPersonaJuridicaDto().getDireccion() != null) {
                personaDto.getPersonaJuridicaDto().getDireccion().setOrigenId(null);
            }
            if (personaDto.getContactos() != null) {
                personaDto.getContactos().forEach(contactoDto -> contactoDto.setOrigen(null));
            }
        }
        return personaDto;
    }
}
