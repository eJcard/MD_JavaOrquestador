package uy.com.md.sistar.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uy.com.md.common.dto.StatusResponseDto;
import uy.com.md.common.util.StatusUtils;
import uy.com.md.sistar.service.SistarbancAPIMonitoringService;

@RestController
@RequestMapping("public")
public class StatusController {

   // @Value("${git.commit.id.abbrev}")
   final String shortCommit = null;

   @Autowired
   SistarbancAPIMonitoringService sistarService;
   
   final BuildProperties buildProperties;

   private static final Logger log = LoggerFactory.getLogger(StatusController.class);

   public StatusController(BuildProperties buildProperties) {
      this.buildProperties = buildProperties;
   }

   @GetMapping(value = "/status")
   public StatusResponseDto status() {
      log.trace("Iniciando llamada a la operación 'status'");
      StatusResponseDto response = new StatusResponseDto();
      Map<String, String> additionalInfo = new HashMap<>();
      try {
         additionalInfo.put("name", buildProperties.getName());
         additionalInfo.put("shortCommit", shortCommit);
      } catch (Exception e) {
         log.error("Error en status. ", e);
         response.setOk(Boolean.FALSE);
         response.setMessage(e.getMessage());
      }

      CompletableFuture<Map<String, String>> checkInfinitus = CompletableFuture.supplyAsync(() -> {
         Map<String, String> map = new HashMap<>();
         try {
            String result = checkInfinitus();
            map.put("infinitusAPI", "UP");
         } catch (Exception e) {
            map.put("infinitusAPI", "DOWN");
            map.put("infinitusAPIErrorReason", e.getMessage());
         }
         return map;
      });

      CompletableFuture<Map<String, String>> checkFito = CompletableFuture.supplyAsync(() -> {
         Map<String, String> map = new HashMap<>();
         try {
            String result = checkFito();
            map.put("fitoAPI", "UP");
            map.put("fitoAPIStatusDetail", result);
         } catch (Exception e) {
            map.put("fitoAPI", "DOWN");
            map.put("fitoAPIErrorReason", e.getMessage());
         }
         return map;
      });

      Map<String, String> combined = Stream.of(checkFito, checkInfinitus)
         .map(CompletableFuture::join)
         .flatMap(m -> m.entrySet().stream())
         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
      
      additionalInfo.putAll(combined);

      response = StatusUtils.retornoStatus(buildProperties.getVersion(), additionalInfo);
      log.trace("Finalizando la llamada a la operacion 'status'");
      return response;
   }

   private String checkInfinitus(){
      return sistarService.getCotizacionDelDia();
   }
     
   private String checkFito(){
      return sistarService.fitoEcho();
   }
}