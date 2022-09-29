package uy.com.md.persona.service.rcor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.persona.dto.rcor.DireccionDto;
import uy.com.md.persona.dto.rcor.PersonaJuridicaDto;
import uy.com.md.persona.model.rcor.Direccion;
import uy.com.md.persona.model.rcor.PersonaJuridica;
import uy.com.md.persona.repository.rcor.DireccionRepository;
import uy.com.md.persona.repository.rcor.PersonaJuridicaRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class PersonaJuridicaService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PersonaJuridicaRepository personaJuridicaRepository;

    @Autowired
    DireccionRepository direccionRepository;

    public PersonaJuridicaDto createPersonaJuridica(String origen, PersonaJuridicaDto personaJuridicaDto) {
        PersonaJuridicaDto dto = new PersonaJuridicaDto();
        PersonaJuridica personaJuridica = modelMapper.map(personaJuridicaDto, PersonaJuridica.class);
        personaJuridica.setOrigenId(origen.toUpperCase());
        if (personaJuridica.getPersonaId() != null) {
            Optional<PersonaJuridica> personaJuridicaAux = personaJuridicaRepository.findByPersonaIdAndOrigenId(personaJuridica.getPersonaId(), origen);
            if (personaJuridicaAux.isPresent()) {
                personaJuridica.setPersonaId(personaJuridicaAux.get().getPersonaId());
            }
        }
        personaJuridica = personaJuridicaRepository.saveAndFlush(personaJuridica);
        if (personaJuridica.getPersonaId() != null) {
            dto = modelMapper.map(personaJuridica, PersonaJuridicaDto.class);
            personaJuridicaDto.getDireccion().setPersonaId(personaJuridicaDto.getPersonaId());
            personaJuridicaDto.getDireccion().setUact(personaJuridicaDto.getUact());
            Direccion direccion = modelMapper.map(personaJuridicaDto.getDireccion(), Direccion.class);
            direccion.setOrigenId(origen.toUpperCase());
            Optional<Direccion> direccionAux = direccionRepository.findByPersonaIdAndOrigenId(personaJuridica.getPersonaId(), origen);
            direccionAux.ifPresent(value -> direccion.setId(value.getId()));
            dto.setDireccion(modelMapper.map(direccionRepository.save(direccion), DireccionDto.class));
        }
        return dto;
    }

    public PersonaJuridicaDto obtener(String origen, Integer personaId) {
        Optional<PersonaJuridica> obj = personaJuridicaRepository.findByPersonaIdAndOrigenId(personaId, origen);
        if (obj.isPresent()) {
            PersonaJuridicaDto dto = modelMapper.map(obj.get(), PersonaJuridicaDto.class);
            Optional<Direccion> direccion = direccionRepository.findByPersonaIdAndOrigenId(personaId, origen.toUpperCase());
            direccion.ifPresent(value -> dto.setDireccion(modelMapper.map(value, DireccionDto.class)));
            return dto;
        } else {
            return null;
        }
    }

    public boolean actualizarPersonaJuridica(String origen, Integer id, PersonaJuridicaDto personaJuridica, String uact) {
        Optional<PersonaJuridica> entity = personaJuridicaRepository.findByPersonaIdAndOrigenId(id, origen.toUpperCase());
        if (entity.isPresent()) {
            entity.get().setFact(LocalDateTime.now());
            entity.get().setUact(uact);
            if (personaJuridica.getNombreFantasia() != null && (entity.get().getNombreFantasia() == null || !entity.get().getNombreFantasia().equals(personaJuridica.getNombreFantasia()))) {
                entity.get().setNombreFantasia(personaJuridica.getNombreFantasia());
            }
            if (personaJuridica.getRazonSocial() != null && (entity.get().getRazonSocial() == null || !entity.get().getRazonSocial().equals(personaJuridica.getRazonSocial()))) {
                entity.get().setRazonSocial(personaJuridica.getRazonSocial());
            }
            Optional<Direccion> direccion = direccionRepository.findByPersonaIdAndOrigenId(id, origen.toUpperCase());
            if (direccion.isPresent()) {
                Direccion dir = modelMapper.map(personaJuridica.getDireccion(), Direccion.class);
                dir.setId(direccion.get().getId());
                direccionRepository.saveAndFlush(dir);
            }
            personaJuridicaRepository.saveAndFlush(entity.get());
            return true;
        }
        return false;
    }

}
