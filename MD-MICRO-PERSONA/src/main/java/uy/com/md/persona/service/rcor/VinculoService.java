package uy.com.md.persona.service.rcor;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.common.util.DateUtil;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.persona.dto.rcor.ObtPersonaPorDocumentoRequestDto;
import uy.com.md.persona.dto.rcor.VinculoRequestDto;
import uy.com.md.persona.dto.rcor.VinculoResponseDto;
import uy.com.md.persona.dto.rcor.VinculosDto;
import uy.com.md.persona.model.rcor.CodTipoVinculo;
import uy.com.md.persona.model.rcor.Persona;
import uy.com.md.persona.model.rcor.Vinculo;
import uy.com.md.persona.repository.rcor.CodTipoVinculoRepository;
import uy.com.md.persona.repository.rcor.PersonaRepository;
import uy.com.md.persona.repository.rcor.VinculoRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VinculoService {

    private static final Logger log = LoggerFactory.getLogger(VinculoService.class);

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    VinculoRepository vinculoRepository;

    @Autowired
    CodTipoVinculoRepository codTipoVinculoRepository;

    @Autowired
    PersonaRepository personaRepository;

    public InternalResponseDto<VinculoResponseDto> crearVinculo(VinculoRequestDto vinculoRequestDto) throws Exception {
        InternalResponseDto<VinculoResponseDto> responseDto = new InternalResponseDto<>();
        responseDto.setResultado(Boolean.FALSE);
        Vinculo vinculo = new Vinculo();
        vinculo.setId(vinculoRequestDto.getId());
        vinculo.setPersonaIdA(obtenerPersona(vinculoRequestDto.getPersonaIdA()));
        vinculo.setPersonaIdB(obtenerPersona(vinculoRequestDto.getPersonaIdB()));
        CodTipoVinculo codTipoVinculo = obtenerTipoVinculo(vinculoRequestDto.getVinculo());
        if (codTipoVinculo != null) {
            vinculo.setCodTipoVinculo(codTipoVinculo);
        } else {
            throw new Exception(String.format("No existe el tipo de vínculo: %s" + vinculoRequestDto.getVinculo()));
        }
        vinculo.setUact(vinculoRequestDto.getUact());
        if (vinculoRequestDto.getFechaDesde() != null) {
            vinculo.setFechaDesde(DateUtil.mapStringToLocalDateTime(vinculoRequestDto.getFechaDesde()));
        }
        if (vinculoRequestDto.getFechaHasta() != null) {
            vinculo.setFechaDesde(DateUtil.mapStringToLocalDateTime(vinculoRequestDto.getFechaHasta()));
        }
        Optional<Vinculo> vinculoAux = vinculoRepository.findByPersonaIdAAndPersonaIdBAndCodTipoVinculo(obtenerPersona(vinculoRequestDto.getPersonaIdA()), obtenerPersona(vinculoRequestDto.getPersonaIdB()), codTipoVinculo);
        vinculoAux.ifPresent(value -> {
            vinculo.setId(value.getId());
            vinculo.setFechaDesde(value.getFechaDesde());
        });

        responseDto.setRespuesta(modelMapper.map(vinculoRepository.save(vinculo), VinculoResponseDto.class));
        responseDto.setResultado(Boolean.TRUE);

        return responseDto;
    }

    public InternalResponseDto<VinculoResponseDto> obtenerVinculo(VinculoRequestDto vinculoDto) {
        InternalResponseDto<VinculoResponseDto> responseDto = new InternalResponseDto<>();
        responseDto.setResultado(Boolean.FALSE);
        Optional<Vinculo> vinculo = vinculoRepository.findByPersonaIdAAndPersonaIdB(obtenerPersona(vinculoDto.getPersonaIdA()), obtenerPersona(vinculoDto.getPersonaIdB()));
        if (vinculo.isPresent()) {
            responseDto.setRespuesta(modelMapper.map(vinculo.get(), VinculoResponseDto.class));
            responseDto.setResultado(Boolean.TRUE);
        }

        return responseDto;
    }

    public InternalResponseDto<List<VinculoResponseDto>> obtenerVinculos(VinculosDto vinculoDto) {
        InternalResponseDto<List<VinculoResponseDto>> responseDto = new InternalResponseDto<>();
        responseDto.setResultado(Boolean.FALSE);
        List<Vinculo> vinculos;
        if (vinculoDto.getVinculo() == null) {
            vinculos = vinculoRepository.findByPersonaIdA(obtenerPersona(vinculoDto.getPersonaIdA()));
        } else {
            vinculos = vinculoRepository.findByPersonaIdAAndCodTipoVinculo(obtenerPersona(vinculoDto.getPersonaIdA()), obtenerTipoVinculo(vinculoDto.getVinculo()));
        }
        responseDto.setRespuesta(mapList(vinculos, VinculoResponseDto.class));
        responseDto.setResultado(Boolean.TRUE);

        return responseDto;
    }

    private Persona obtenerPersona(ObtPersonaPorDocumentoRequestDto dto) {
        Optional<Persona> persona = personaRepository.obtPersonaPorDocumento(dto.getPaisDoc(), dto.getTipoDoc(), dto.getNumDoc());
        return persona.orElse(null);
    }

    private CodTipoVinculo obtenerTipoVinculo(String relacion) {
        Optional<CodTipoVinculo> codTipoVinculo = codTipoVinculoRepository.findByNombreIgnoreCase(relacion.toUpperCase());
        return codTipoVinculo.orElse(null);
    }

    public boolean eliminarVinculo(Integer personaId) {
        boolean resultado = Boolean.FALSE;
        Optional<Persona> persona = personaRepository.findById(personaId);
        if (persona.isPresent()) {
            List<Vinculo> vinculos = vinculoRepository.findByPersonaIdA(persona.get());
            for (Vinculo vinculo : vinculos) {
                try {
                    vinculoRepository.delete(vinculo);
                    resultado = Boolean.TRUE;
                } catch (Exception e) {
                    resultado = Boolean.FALSE;
                    log.error("Error eliminando el vinculo con persona_id_a: " + personaId, e);
                }
            }
        }
        return resultado;
    }

    private <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source
                .stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }
}
