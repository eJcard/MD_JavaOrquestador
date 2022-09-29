package uy.com.md.jcard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.common.util.Constantes;
import uy.com.md.modelo.mdw.entity.Sucursal;
import uy.com.md.modelo.mdw.repository.SucursalRepository;

import java.util.Optional;

@Service
public class SucursalService {

    @Autowired
    SucursalRepository sucursalRepository;

    public String obtenerSucursal(String sucursal, Long red, String procesadora) {
        return obtenerSucursalPorCodigoYProcesadoraa(obtenerSucursalPorCodigoYRed(sucursal, red), procesadora);
    }

    private String obtenerSucursalPorCodigoYProcesadoraa(Optional<Sucursal> sucursal, String procesadora) {
        String suc;
        switch (procesadora) {
            case Constantes.JCARD_NAME:
                suc = sucursal.map(Sucursal::getCodigo_jcard).orElse(null);
                break;
            case Constantes.GLOBAL_NAME:
                suc = sucursal.map(Sucursal::getCodigo_global).orElse(null);
                break;
            case Constantes.SISTARBANC_NAME:
                suc = sucursal.map(Sucursal::getCodigo_sistarbanc).orElse(null);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return suc;
    }

    public Optional<Sucursal> obtenerSucursalPorCodigoYRed(String codigo, Long red) {
        return sucursalRepository.findByCodigoAndRedId(codigo, red);
    }
}
