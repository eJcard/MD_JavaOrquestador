package uy.com.md.persona.service.rcor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.persona.dto.rcor.DireccionDto;
import uy.com.md.persona.dto.rcor.PersonaFisicaDto;
import uy.com.md.persona.model.rcor.Direccion;
import uy.com.md.persona.model.rcor.PersonaFisica;
import uy.com.md.persona.repository.rcor.DireccionRepository;
import uy.com.md.persona.repository.rcor.PersonaFisicaRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class PersonaFisicaService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PersonaFisicaRepository personaFisicaRepository;

    @Autowired
    DireccionRepository direccionRepository;

    public PersonaFisicaDto createPersonaFisica(String origen, PersonaFisicaDto personaFisicaDto) {
        PersonaFisicaDto dto = new PersonaFisicaDto();
        PersonaFisica personaFisica = modelMapper.map(personaFisicaDto, PersonaFisica.class);
        personaFisica.setOrigenId(origen.toUpperCase());
        if (personaFisica.getPersonaId() != null) {
            Optional<PersonaFisica> personaFisicaAux = personaFisicaRepository.findByPersonaIdAndOrigenId(personaFisica.getPersonaId(), origen);
            if (personaFisicaAux.isPresent()) {
                personaFisica.setPersonaId(personaFisicaAux.get().getPersonaId());
            }
        }
        personaFisica = personaFisicaRepository.saveAndFlush(personaFisica);
        if (personaFisica.getPersonaId() != null) {
            dto = modelMapper.map(personaFisica, PersonaFisicaDto.class);
            personaFisicaDto.getDireccion().setPersonaId(personaFisica.getPersonaId());
            personaFisicaDto.getDireccion().setUact(personaFisicaDto.getUact());
            Direccion direccion = modelMapper.map(personaFisicaDto.getDireccion(), Direccion.class);
            direccion.setOrigenId(origen.toUpperCase());
            Optional<Direccion> direccionAux = direccionRepository.findByPersonaIdAndOrigenId(personaFisica.getPersonaId(), origen);
            direccionAux.ifPresent(value -> direccion.setId(value.getId()));
            dto.setDireccion(modelMapper.map(direccionRepository.save(direccion), DireccionDto.class));
        }

        return dto;
    }

    public PersonaFisicaDto obtener(String origen, Integer personaId) {
        Optional<PersonaFisica> obj = personaFisicaRepository.findByPersonaIdAndOrigenId(personaId, origen.toUpperCase());
        if (obj.isPresent()) {
            PersonaFisicaDto dto = modelMapper.map(obj.get(), PersonaFisicaDto.class);
            Optional<Direccion> direccion = direccionRepository.findByPersonaIdAndOrigenId(personaId, origen.toUpperCase());
            direccion.ifPresent(value -> dto.setDireccion(modelMapper.map(value, DireccionDto.class)));
            return dto;
        } else {
            return null;
        }
    }

    public boolean actualizarPersonaFisica(String origen, Integer id, PersonaFisicaDto personaFisica, String uact) {
        Optional<PersonaFisica> entity = personaFisicaRepository.findByPersonaIdAndOrigenId(id, origen.toUpperCase());
        if (entity.isPresent()) {
            entity.get().setFact(LocalDateTime.now());
            entity.get().setUact(uact);
            if (personaFisica.getActividadEconomicaTipoSectorId() != null && (entity.get().getActividadEconomicaTipoSectorId() == null || entity.get().getActividadEconomicaTipoSectorId() != personaFisica.getActividadEconomicaTipoSectorId())) {
                entity.get().setActividadEconomicaTipoSectorId(personaFisica.getActividadEconomicaTipoSectorId());
            }
            if (personaFisica.getCodPaisNacId() != null && (entity.get().getCodPaisNacId() == null || entity.get().getCodPaisNacId() != personaFisica.getCodPaisNacId())) {
                entity.get().setCodPaisNacId(personaFisica.getCodPaisNacId());
            }
            if (personaFisica.getFechaNac() != null && (entity.get().getFechaNac() == null || !entity.get().getFechaNac().equals(personaFisica.getFechaNac()))) {
                entity.get().setFechaNac(personaFisica.getFechaNac());
            }
            if (personaFisica.getCodEstadoCivilId() != null && (entity.get().getCodEstadoCivilId() == null || !entity.get().getCodEstadoCivilId().equals(personaFisica.getCodEstadoCivilId()))) {
                entity.get().setCodEstadoCivilId(personaFisica.getCodEstadoCivilId());
            }
            if (personaFisica.getCodSexoId() != null && (entity.get().getCodSexoId() == null || !entity.get().getCodSexoId().equals(personaFisica.getCodSexoId()))) {
                entity.get().setCodSexoId(personaFisica.getCodSexoId());
            }
            if (personaFisica.getPrimerApellido() != null && (entity.get().getPrimerApellido() == null || !entity.get().getPrimerApellido().equals(personaFisica.getPrimerApellido()))) {
                entity.get().setPrimerApellido(personaFisica.getPrimerApellido());
            }
            if (personaFisica.getPrimerNombre() != null && (entity.get().getPrimerNombre() == null || !entity.get().getPrimerNombre().equals(personaFisica.getPrimerNombre()))) {
                entity.get().setPrimerNombre(personaFisica.getPrimerNombre());
            }
            if (personaFisica.getSegundoApellido() != null && (entity.get().getSegundoApellido() == null || !entity.get().getSegundoApellido().equals(personaFisica.getSegundoApellido()))) {
                entity.get().setSegundoApellido(personaFisica.getSegundoApellido());
            }
            if (personaFisica.getSegundoNombre() != null && (entity.get().getSegundoNombre() == null || !entity.get().getSegundoNombre().equals(personaFisica.getSegundoNombre()))) {
                entity.get().setSegundoNombre(personaFisica.getSegundoNombre());
            }

            Optional<Direccion> direccion = direccionRepository.findByPersonaIdAndOrigenId(id, origen.toUpperCase());
            if (direccion.isPresent()) {
                Direccion dir = modelMapper.map(personaFisica.getDireccion(), Direccion.class);
                dir.setId(direccion.get().getId());
                direccionRepository.saveAndFlush(dir);
            }
            personaFisicaRepository.saveAndFlush(entity.get());
            return true;
        }
        return false;
    }

}
