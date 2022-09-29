package uy.com.md.persona.service.rcor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.persona.dto.rcor.ContactoDto;
import uy.com.md.persona.dto.rcor.PersonaDto;
import uy.com.md.persona.model.rcor.Contacto;
import uy.com.md.persona.repository.rcor.ContactoRepository;
import uy.com.md.persona.repository.rcor.OrigenRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
public class ContactoService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ContactoRepository contactoRepository;

    @Autowired
    OrigenRepository origenRepository;

    public List<ContactoDto> persistContactos(String origen, PersonaDto persona, List<ContactoDto> contactos) {
        List<ContactoDto> response = new ArrayList<>();
        if (persona.getId() != null) {
            for (ContactoDto contacto : contactos) {
                Contacto obj = modelMapper.map(contacto, Contacto.class);
                obj.setPersonaId(persona.getId());
                obj.setOrigen(origenRepository.getOne(origen));
                Optional<Contacto> contactoAux = contactoRepository.findByPersonaIdAndOrigenIdAndEtiqueta(persona.getId(), origen, obj.getEtiqueta());
                if (contactoAux.isPresent()) {
                    obj.setId(contactoAux.get().getId());
                }
                ContactoDto dto = modelMapper.map(contactoRepository.saveAndFlush(obj), ContactoDto.class);
                response.add(dto);
            }
        }
        return response;
    }

    public ContactoDto obtener(String origen, Integer id) {
        return modelMapper.map(contactoRepository.findByIdAndOrigenId(id, origen.toUpperCase()), ContactoDto.class);
    }

    private <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source
                .stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }

    public List<ContactoDto> obtenerListaContactosPersona(Integer personaId, String origen) {
        List<Contacto> objs = contactoRepository.findByPersonaIdAndOrigenId(personaId, origen.toUpperCase());
        List<ContactoDto> dtos = mapList(objs, ContactoDto.class);
        return dtos;
    }

    public void actualizarContactosPersona(Integer personaId, List<ContactoDto> contactos, String uact, String origen) {
        for (ContactoDto dto : contactos) {
            Optional<Contacto> entity = contactoRepository.findByPersonaIdAndOrigenIdAndEtiqueta(personaId, origen.toUpperCase(), dto.getEtiqueta());
            if (entity.isPresent()) {
                if (dto.getContacto() != null && (entity.get().getContacto() == null || !entity.get().getContacto().equals(dto.getContacto()))) {
                    entity.get().setContacto(dto.getContacto());
                    entity.get().setFact(LocalDateTime.now());
                    entity.get().setUact(uact);
                    entity.get().setOrigen(origenRepository.getOne(origen.toUpperCase()));
                    contactoRepository.saveAndFlush(entity.get());
                }
            } else {
                Contacto obj = modelMapper.map(dto, Contacto.class);
                obj.setPersonaId(personaId);
                obj.setFact(LocalDateTime.now());
                obj.setUact(uact);
                obj.setOrigen(origenRepository.getOne(origen.toUpperCase()));
                contactoRepository.saveAndFlush(obj);
            }
        }
    }

}
