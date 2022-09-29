package uy.com.md.global.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.modelo.mdw.entity.Sucursal;
import uy.com.md.modelo.mdw.repository.SucursalRepository;

import java.util.Optional;

@Service
public class SucursalService {

    @Autowired
    SucursalRepository sucursalRepository;

    private String getSucursal(Optional<Sucursal> sucursal){
        return sucursal.map(Sucursal::getCodigo_jcard).orElse(null);
    }

    private Optional<Sucursal> getSucursal(Long sucursalId){
        return sucursalRepository.findById(sucursalId);
    }
}
