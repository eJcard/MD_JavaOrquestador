package uy.com.md.persona.service.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.persona.model.rcor.CodTipoDoc;
import uy.com.md.persona.repository.rcor.CodTipoDocRepository;

import java.util.Optional;

@Service
public class CodTipoDocService {

    @Autowired
    CodTipoDocRepository codTipoDocRepository;

    public String getCodigoTipoDocumento(int codigo) {
        String tipoDocumento = "";
        Optional<CodTipoDoc> codTipoDoc = codTipoDocRepository.findById(codigo);
        if (codTipoDoc.isPresent()) {
                tipoDocumento = codTipoDoc.get().getAlpha();
        }
        return tipoDocumento;
    }
}
