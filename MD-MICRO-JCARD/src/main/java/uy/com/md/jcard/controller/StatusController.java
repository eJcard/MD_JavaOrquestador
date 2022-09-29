package uy.com.md.jcard.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import uy.com.md.common.dto.StatusResponseDto;
import uy.com.md.common.util.StatusUtils;

@RestController
@RequestMapping("public")
public class StatusController {

	@Autowired
	BuildProperties buildProperties;

	@Autowired
	GitProperties gitProperties;

	private static Logger log = LoggerFactory.getLogger(StatusController.class);

	@GetMapping(value = "/status")
	@ResponseBody
	public StatusResponseDto status() {
		log.trace("Iniciando llamada a la operación 'status'");

		StatusResponseDto response = new StatusResponseDto();
		try {		
			Map<String, String> additionalInfo = new HashMap<>();
			additionalInfo.put("name", buildProperties.getName());
			additionalInfo.put("commit", gitProperties.getShortCommitId());
			response = StatusUtils.retornoStatus(buildProperties.getVersion(), additionalInfo);
		} catch (Exception e) {
			log.error("Error en status. ", e);
			response.setOk(Boolean.FALSE);
			response.setMessage(e.getMessage());
		}
		log.trace("Finalizando la llamada a la operacion 'status'");
		return response;
	}
}