package uy.com.md.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import uy.com.md.common.dto.StatusResponseDto;

public final class StatusUtils {

	private StatusUtils() {
		super();
	}

	public static StatusResponseDto retornoStatus(String version, Map<String, String> additionalInfo) {
		StatusResponseDto retorno = new StatusResponseDto();
		retorno.setOk(Boolean.TRUE);
		retorno.setMessage("OK");
		retorno.setVersion(version);

		if(additionalInfo == null) {
			additionalInfo = new HashMap<>();
		}
		SimpleDateFormat sdF = new SimpleDateFormat(Constantes.FORMATO_ISO);
		additionalInfo.put("currentDate", sdF.format(new Date()));
		retorno.setAdditionalInfo(additionalInfo);
		return retorno;
	}

}
