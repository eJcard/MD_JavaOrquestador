package uy.com.md.sistar.monitoring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import uy.com.md.sistar.service.SistarbancAPIMonitoringService;

@Component("infinitusAPI")
public class SistarInfinitusIndicator implements HealthIndicator {

  @Autowired
  SistarbancAPIMonitoringService sistarService;
  
  @Override
  public Health health() {
    try{
      String result = check();
      return Health.up().build();
    }catch(Exception ex){
      return Health.down().withDetail("errorMessage", ex.toString()).build();
    }
  }

  private String check(){
    return sistarService.getCotizacionDelDia();
  }
}
