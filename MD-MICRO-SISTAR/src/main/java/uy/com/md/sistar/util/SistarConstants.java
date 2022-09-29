package uy.com.md.sistar.util;

public interface SistarConstants {
	String ACCOUNT_NUMBER_PATTERN = "^\\d{10,18}$";
	String EMAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
	String COUNTRY_CODE_PATTERN = "^\\d{1,3}$";
	String COUNTRY_CODE_DESCRIPTION = "ISO Country code (numeric)";
	String ZIP_CODE_PATTERN = "^[0-9A-Za-z]{3,10}%";
	String PHONE_NUMBER_PATTERN = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$";
	String NUMBERX3_CODE_PATTERN = "^\\d{1,3}$";
	String NUMBERX2_CODE_PATTERN = "^\\d{1,2}$";
	String NUMBERX1_CODE_PATTERN = "^\\d{1}$";
	String RRN_PATTERN = "^\\d{1,8}$";
	String INVOICE_NUMBER_PATTERN = "^[0-9a-zA-Z]{1,12}$";
	String ID_PATTERN = "^[0-9a-zA-Z]{1,12}$";
	String ID_EXAMPLE = "42588947";
	String ID_DESCRIPTION = "Document number";
	String CARDHOLDER_COMPOSITE_ID_PATTERN = "^\\d{1,3}\\.\\d{1,2}\\.[0-9a-zA-Z]{3,12}$";
	String CARDHOLDER_COMPOSITE_ID_EXT_PATTERN = "^\\d{1,3}\\.\\d{1,2}\\.[0-9a-zA-Z]{3,12}.\\d{1,2}$";
	String ID_TYPE_DESCRIPTION = "Document type code";
	String ACCOUNT_NUMBER_EXAMPLE = "370100000113";
	String DATE_FORMAT = "^20\\d{2}-[0-1]\\d-[0-3]\\d$";
	String MCC_PATTERN = "\\d{4}";
	String TXNID_PATTERN = "\\d{1,18}";
	String NUMERIC_PATTERN = "[1-9][0-9]+";

	String SOAP_ACTION_ABM_CUENTA = "ConsultasVisaWebTesting/aSistarbancMDF2.ABMCUENTATARJETA";
	String SOAP_ACTION_RECARGA_CI = "http://tempuri.org/WSPagosAjustesxCI";
	String SOAP_ACTION_RECARGA_CTA = "http://tempuri.org/WSPagosAjustes";
	String SOAP_ACTION_GET_PRODUCTOS = "http://tempuri.org/TarjetasxCI";
	String SOAP_ACTION_REVERSO_POR_DOC = "http://tempuri.org/WSPagosAjustesxCI";
	String SOAP_ACTION_REVERSO_POR_CTA = "http://tempuri.org/WSPagosAjustes";
	String SOAP_ACTION_DISPONIBLE = "http://tempuri.org/ConsultaDisponibleSistarbanc";
	String SOAP_ACTION_GET_MOVIMIENTOS = "SistarbancPayGroupaction/ASISTARBANCPAYGROUPV2.MOVENPERIODO";
	String SOAP_ACTION_ALTA_INNOMINADA = "http://tempuri.org/CrearTarjetaPrepagaInnominada";
	String SOAP_ACTION_REEMPLAZO_INNOMINADA_XCI = "http://tempuri.org/ReemplazoTarjetaPrepagaInnominadaXCi";
	String SOAP_ACTION_RENOVACION_INNOMINADA_XCI = "http://tempuri.org/RenovacionTarjetaPrepagaInnominadaXCi";
	String SOAP_ACTION_REEMPLAZO_INNOMINADA_XCUENTA = "http://tempuri.org/ReemplazoTarjetaPrepagaInnominada";
	String SOAP_ACTION_C2C_TRANSFERENCIA = "http://tempuri.org/TransferenciaC2C";
	String SOAP_ACTION_C2C_REVERSO = "http://tempuri.org/RevertirTransferenciaC2C";
	String SOAP_ACTION_C2B_AUTORIZACION = "ConsultasVISAWebaction/ASISTARBANCMDTRFC2B.AUTORIZACION";
	String SOAP_ACTION_C2B_CONFIRMACION = "ConsultasVISAWebaction/ASISTARBANCMDTRFC2B.CONFIRMACION";
	String SOAP_ACTION_C2B_REVERSO = "ConsultasVISAWebaction/ASISTARBANCMDTRFC2B.REVERSO";
	String SOAP_ACTION_ACTUALIZAR_ESTADO_TARJETA = "ConsultasVISAWebaction/ASISTARBANCMDF2.CAMBIOESTADOTARJETA";
	String SOAP_ACTION_HABILITAR_INHABILITAR = "AppAuxWsaction/AWSCAMBIOESTADO_CUENTA.Execute";
	String SOAP_ACTION_CONSULTA_ESTADO_TARJETA = "ConsultasVISAWebaction/ASISTARBANCMDF25.STATUSCUENTATARJETAS";
	String SOAP_ACTION_PAGO_SERVICIO_CAJA = "http://tempuri.org/PagoServicioCajaPrepaga";
	String SOAP_ACTION_FITO_ECHO = "ConsultasVISAWebaction/ASISTARBANCMDF25.ECHOTEST";
	String SOAP_ACTION_TASA_DEL_DIA_DOLARES = "http://tempuri.org/Tasa_del_Dia_Dolares";
	String SOAP_ACTION_CONSULTA_TRANSACCION = "http://tempuri.org/ConsultaPorReferencia";
	String SOAP_ACTION_EXCEPTION_FRAUDE_MODI = "http://tempuri.org/ExceptionFraudeModi";
	String SOAP_ACTION_EXCEPTION_FRAUDE_ALTA = "http://tempuri.org/ExceptionFraudeAlta";
	String SOAP_ACTION_EXCEPTION_FRAUDE_CONSUL = "http://tempuri.org/ExceptionFraudeConsul";

	Short CREDIT_LIMIT_PORC = 100;
	int MAX_EXTERNAL_ID_INNOMINADA = 12;
	int MAX_TRX_ID_NOMINADA = 12;
	int MAX_EXTERNAL_ID_TRANSACTIONS = 8;
	String OPERATION_CODE_ALTA = "A";
	String REPRESENTATIVE_RELATION_CODE = "N";
	String UNDERAGE_RELATION_CODE = "A";
	String HOLDER_RELATION_CODE = "T";
	String CTA_TIPO_PERSONAL = "P";
	String CE_NO_IMPRIME = "NI";
	String CARTERA_CUENTA_STANDARD = "0001";
	String ITC_C2B = "R200.40.01";
	String ITC_C2P = "T200.40.01";
	String ITC_P2C = "T200.40.21";

	String UNKNOWN_ERR_CODE = "unknown.error";
	String QUERY_TRANSACTION_ERR_CODE = "transactions.request.error";
	String UNKNOWN_ERR_DESC = "Unknown error";

	String ITC_RECHARGE_AUTHORIZATION = "200.21";
}
