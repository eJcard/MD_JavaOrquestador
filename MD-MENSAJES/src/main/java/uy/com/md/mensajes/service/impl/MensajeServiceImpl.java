package uy.com.md.mensajes.service.impl;

import javax.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uy.com.md.mensajes.dto.MensajeDetail;
import uy.com.md.mensajes.enumerated.MensajeType;
import uy.com.md.mensajes.interfaces.MensajeNegocioInterface;
import uy.com.md.mensajes.interfaces.MensajeOrigenInterface;
import uy.com.md.mensajes.model.CodMensajeExternoEntity;
import uy.com.md.mensajes.model.CodMensajeNegocioEntity;
import uy.com.md.mensajes.repository.CodMensajeExternoRepository;
import uy.com.md.mensajes.repository.CodMensajeNegocioRepository;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Constantes;
import uy.com.md.mensajes.util.Origenes;

@Service
public class MensajeServiceImpl implements MensajeService {

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	CodMensajeExternoRepository codMensajeExternoRepository;

	@Autowired
	CodMensajeNegocioRepository codMensajeNegocioRepository;

	@Override
	public MensajeDetail obtenerMensajeDetail(MensajeOrigenInterface mensajeOrigen) {
		CodMensajeExternoEntity codMensajeExternoEntityDefault = modelMapper.map(mensajeOrigen, CodMensajeExternoEntity.class);
		CodMensajeNegocioEntity codMensajeNegocioEntityDefault = new CodMensajeNegocioEntity();
		codMensajeNegocioEntityDefault.setCodigoNegocio(mensajeOrigen.getCodigo());
		codMensajeNegocioEntityDefault.setMensajeNegocio(mensajeOrigen.getMensajeExterno());
		codMensajeNegocioEntityDefault.setSeveridad(MensajeType.ERROR.getLabel());
		codMensajeExternoEntityDefault.setCodMensajeNegocio(codMensajeNegocioEntityDefault);
		String code = mensajeOrigen.getCodigo().length() <= Constantes.COLUMN_CODIGO_LENGTH ? mensajeOrigen.getCodigo() : mensajeOrigen.getCodigo().substring(0, Constantes.COLUMN_CODIGO_LENGTH);

		CodMensajeExternoEntity codMensajeExternoEntity = codMensajeExternoEntityDefault;

		try{
			codMensajeExternoEntity = codMensajeExternoRepository.findFirstByOrigenAndCodigo(mensajeOrigen.getOrigen(), code).orElseGet(() -> codMensajeExternoEntityDefault);
		}catch(Exception ignored){}

		MensajeType errorType = MensajeType.valueOfLabel(codMensajeExternoEntity.getCodMensajeNegocio().getSeveridad());
		return new MensajeDetail(codMensajeExternoEntity.getOrigen(), codMensajeExternoEntity.getCodMensajeNegocio().getCodigoNegocio(), codMensajeExternoEntity.getCodMensajeNegocio().getMensajeNegocio(), errorType);
	}

	@Override
	public MensajeDetail obtenerMensajeDetail(MensajeNegocioInterface mensaje) {
		String code = mensaje.getCodigoNegocio().length() <= Constantes.COLUMN_CODIGO_LENGTH ? mensaje.getCodigoNegocio() : mensaje.getCodigoNegocio().substring(0, Constantes.COLUMN_CODIGO_LENGTH);

		CodMensajeNegocioEntity codMensajeNegocioEntity = codMensajeNegocioRepository.findFirstByCodigoNegocio(code).orElseThrow(EntityNotFoundException::new);
		MensajeType errorType = MensajeType.valueOfLabel(codMensajeNegocioEntity.getSeveridad());
		return new MensajeDetail(Origenes.MIDINERO, codMensajeNegocioEntity.getCodigoNegocio(), codMensajeNegocioEntity.getMensajeNegocio(), errorType);
	}

}
