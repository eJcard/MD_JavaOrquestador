package uy.com.md.global.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uy.com.md.common.util.Constantes;
import uy.com.md.global.dto.*;
import uy.com.md.global.service.*;
import uy.com.md.global.soap.client.aamdsoapautorizarpago.AmdSoapAutorizarPagoExecuteResponse;
import uy.com.md.global.soap.client.aamdsoapobtenerdatosxcuenta.AmdSoapObtenerDatosXCuentaExecuteResponse;
import uy.com.md.global.soap.client.aamdsoapobtenermismovimientos.AmdSoapObtenerMisMovimientosV2ExecuteResponse;
import uy.com.md.global.soap.client.aamdsoaprecargartarjeta.AmdSoapRecargarTarjetaExecuteResponse;
import uy.com.md.global.soap.client.aamdsoaptransferirc2banco.AmdSoapTransferirC2BancoExecuteResponse;
import uy.com.md.global.soap.client.aamdsoaptransferirc2c.AmdSoapTransferirC2CExecuteResponse;
import uy.com.md.global.soap.client.solicitudproductonominado.AmdSoapSolicitudProductoNominadoV2ExecuteResponse;
import uy.com.md.global.soap.client.aamdsoapentreganominada.AmdSoapEntregaNominadaExecuteResponse;
import uy.com.md.mensajes.dto.InternalResponseDto;

@RestController
@RequestMapping("global")
public class GlobalController {

    @Autowired
    SolicitudService solicitudService;

    @Autowired
    EntregaService entregaService;

    @Autowired
    RecargaService recargaService;

    @Autowired
    MovimientosService movimientosService;
    
    @Autowired
    TransferenciaC2CService transferenciaC2CService;
    
    @Autowired
    PagoServiciosService pagoServiciosService;

    @Autowired
    InformacionService informacionService;

    @Autowired
    TransferenciaC2BService transferenciaC2BService;

    @PostMapping("producto-nominado/solicitud")
    public ResponseEntity<InternalResponseDto<AmdSoapSolicitudProductoNominadoV2ExecuteResponse>> solicitud(@RequestHeader(Constantes.AUTH_TOKEN) String token, @RequestHeader(Constantes.REQUEST_ID) String uuid, @RequestBody SolcitudRequestDto solcitudRequestDto) {
        return new ResponseEntity<>(solicitudService.solicitud(token, uuid, solcitudRequestDto), HttpStatus.OK);
    }

    @PostMapping("producto-nominado/entrega")
    public ResponseEntity<InternalResponseDto<AmdSoapEntregaNominadaExecuteResponse>> entrega(@RequestHeader(Constantes.AUTH_TOKEN) String token, @RequestHeader(Constantes.REQUEST_ID) String uuid, @RequestBody EntregaRequestDto entregaProductoNominadoRequestDto) {
        return new ResponseEntity<>(entregaService.entrega(token, uuid, entregaProductoNominadoRequestDto), HttpStatus.OK);
    }

    @PostMapping("producto-nominado/recarga")
    public ResponseEntity<InternalResponseDto<AmdSoapRecargarTarjetaExecuteResponse>> recarga(@RequestHeader(Constantes.AUTH_TOKEN) String token, @RequestHeader(Constantes.REQUEST_ID) String uuid, @RequestBody RecargaRequestDto recargaRequestDto) {
        return new ResponseEntity<>(recargaService.recarga(token, uuid, recargaRequestDto), HttpStatus.OK);
    }

    @PostMapping("producto-nominado/movimientos")
    public ResponseEntity<InternalResponseDto<AmdSoapObtenerMisMovimientosV2ExecuteResponse>> movimientos(@RequestHeader(Constantes.AUTH_TOKEN) String token, @RequestHeader(Constantes.REQUEST_ID) String uuid, @RequestBody MovimientosRequestDto movimientosRequestDto) {
        return new ResponseEntity<>(movimientosService.movimientos(token, uuid, movimientosRequestDto), HttpStatus.OK);
    }

    @PostMapping("producto-nominado/transferencias-c2c")
    public ResponseEntity<InternalResponseDto<AmdSoapTransferirC2CExecuteResponse>> transferirSaldosC2C(@RequestHeader(Constantes.AUTH_TOKEN) String token, @RequestHeader(Constantes.REQUEST_ID) String uuid, @RequestBody TransferenciaRequestDto transferenciaC2CRequestDto) {
        return new ResponseEntity<>(transferenciaC2CService.transferirSaldosC2C(token, uuid, transferenciaC2CRequestDto), HttpStatus.OK);
    }
    
    @PostMapping("producto-nominado/pago-servicios")
    public ResponseEntity<InternalResponseDto<AmdSoapAutorizarPagoExecuteResponse>> pagoServicios(@RequestHeader(Constantes.AUTH_TOKEN) String token, @RequestHeader(Constantes.REQUEST_ID) String uuid, @RequestBody AutorizarPagoRequestDto autorizarPagoRequestDto) {
        return new ResponseEntity<>(pagoServiciosService.pagoServicios(token, uuid, autorizarPagoRequestDto), HttpStatus.OK);
    }

    @PostMapping("producto-nominado/informacion")
    public ResponseEntity<InternalResponseDto<AmdSoapObtenerDatosXCuentaExecuteResponse>> informacionCuenta(@RequestHeader(Constantes.AUTH_TOKEN) String token, @RequestHeader(Constantes.REQUEST_ID) String uuid, @RequestBody CuentaDto cuentaDto) {
        return new ResponseEntity<>(informacionService.informacionCuenta(token, uuid, cuentaDto), HttpStatus.OK);
    }

    @PostMapping("producto-nominado/transferencias-c2b")
    public ResponseEntity<InternalResponseDto<AmdSoapTransferirC2BancoExecuteResponse>> transferirSaldosC2B(@RequestHeader(Constantes.AUTH_TOKEN) String token, @RequestHeader(Constantes.REQUEST_ID) String uuid, @RequestBody TransferenciaRequestDto transferenciaC2CRequestDto) {
        return new ResponseEntity<>(transferenciaC2BService.transferirSaldosC2B(token, uuid, transferenciaC2CRequestDto), HttpStatus.OK);
    }
}
