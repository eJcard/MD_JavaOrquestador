package uy.com.md.mensajes.service;

import org.springframework.stereotype.Service;

import uy.com.md.mensajes.dto.*;
import uy.com.md.mensajes.interfaces.*;


@Service
public interface MensajeService {

	MensajeDetail obtenerMensajeDetail(MensajeOrigenInterface mensaje);
	
	MensajeDetail obtenerMensajeDetail(MensajeNegocioInterface mensaje);

}
