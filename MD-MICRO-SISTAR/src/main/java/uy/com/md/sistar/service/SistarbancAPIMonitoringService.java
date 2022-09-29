package uy.com.md.sistar.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uy.com.md.sistar.soap.SistarClient;

@Service
public class SistarbancAPIMonitoringService {

  private final SistarClient sistarClientMDF25;
  private final SistarClient sistarClientWSMidinero;

  public SistarbancAPIMonitoringService(
      @Qualifier("SistarClientMDF25") SistarClient sistarClientMDF25,
      @Qualifier("SistarClientWSMidinero") SistarClient sistarClientWSMidinero){
    this.sistarClientMDF25 = sistarClientMDF25;
    this.sistarClientWSMidinero = sistarClientWSMidinero;
  }

  public String fitoEcho() {
    return sistarClientMDF25.fitoEchoTest().getResult();
  }

  public String getCotizacionDelDia() {
    return sistarClientWSMidinero.getCotizacionDelDia().getTasaDelDiaDolaresResult().toString();
  }
}
