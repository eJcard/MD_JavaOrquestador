package uy.com.md.persona.service.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.persona.model.rcor.CodPais;
import uy.com.md.persona.repository.rcor.CodPaisRepository;

import java.util.Optional;

@Service
public class CodPaisService {

    @Autowired
    CodPaisRepository codPaisRepository;

    public String getCodigoPais(int codigo) {
        String pais = "";
        Optional<CodPais> codPais = codPaisRepository.findById(codigo);
        if (codPais.isPresent()) {
                pais = codPais.get().getAlpha2();

        }
        return pais;
    }
}
