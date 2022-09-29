package uy.com.md.sistar.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@ConfigurationProperties(prefix = "sistar")
@Data
public class SistarServiceProperties {
   @Data
   public static class Issuer {
      String name;
   }
   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   public static class Currency{
      private String symbol;
      private String sbCode;
      private String desc;
   }
   @Data
   public static class SBTrnCodes{
      String code;
      String subcode;
   }

   public static class Product extends SBTrnCodes{}

   public static class Channel extends SBTrnCodes {}

   public static class Mcc extends SBTrnCodes{}

   private String[] issuers;
   private Map<String, Issuer> issuers_by_id = new HashMap<>();
   private Map<String, Currency> currencies = new HashMap<>();
   private Map<String, String> itc = new HashMap<>();

   /*
   * Diccionario con codigos SB y sus ITC correspondientes.
   * clave: del estilo sbcode-X-[Y|default], donde X e Y son el codigo y subcodigo SB
   * valor: código ITC
   * */
   private Map<String, String> itc_by_sbcode = new HashMap<>();

   /**
   * Diccionario con codigos de producto y sus codigos SB que serán utilizados para categorizar las recargas solicitadas a API Sistar.
   * clave: [id-X|default], donde X es el codigo de producto Midinero. Ej: 41
   * valor: {@link Product} conteniendo informacion sobre los codigos SB
   * */
   private Map<String, Product> products = new HashMap<>();

   /**
   * Diccionario con codigos de producto y sus codigos SB que serán utilizados para categorizar las recargas solicitadas a API Sistar.
   * clave: <nombre-canal>, donde nombre-canal es el nombre del canal relacionado a la recarga. Ej: banco
   * valor: {@link Channel} conteniendo informacion sobre los codigos SB
   * */
   private Map<String, Channel> channels = new HashMap<>();

   String alternative_itc_mapping_enabled = "false";
   Boolean force_merchant_id = false;
   Boolean exclude_void_withdraws = true;
   
   // Excluye anulaciones del resultado de consulta de movimientos
   Boolean exclude_voids = true;
   Map<String, Boolean> invert_amounts = new HashMap<>();
   Boolean active_filter_country = true;
   Boolean replace_card_by_account_enabled = true;
   List<String> available_cards_blocking_codes = Arrays.asList("0", "98", "99", "95");
   Integer default_branch_office = 998;
   Set<String> mock_account = new HashSet<>();
   Boolean enable_mock = false;
   Map<String, Boolean> filter_code = new HashMap<>();
   Map<String, String> movement_description = new HashMap<>();
   Map<String, String> movement_merchant = new HashMap<>();
   Map<String, Boolean> filter_products = new HashMap<>();
   Map<String, String> mcc_code_sbcode = new HashMap<>();

   // Antiguedad maxima (en meses) de movimientos que se devuelve en la consulta de movimientos.
   // Por ejemplo, si se asigna 0 (cero), solo se permite consultar movimientos del mes corriente (desde el día 1),
   // si se asigna uno, entonces se devuelven movimientos como maximo del dia 1 del mes anterior.
   // En general el valor se la property va a ser: <nro. maximo de meses> - 1
   Integer max_months = null;

   // Devuelve saldo cero siempre que el saldo sea > 0
   Boolean set_negative_balance_to_cero = false;

   // Longitud máxima del nombre/apellido aplicada al alta (si la longitud es excedida, se trunca).
   Integer lastName_max_length = 25;
   
   // Longitud máxima del campo direccion aplicada al alta (si la longitud es excedida, se trunca).
   Integer addr_max_length = 25;

   // Codigos de tipo de documento para los que se fuerza un codigo de pais de emision igual a "000", sin importar el valor
   // indicado en el request entrante 
   private List<String> doc_type_without_country = Arrays.asList("2", "5", "17");
   
   // Elimina movimientos recibidos de SB en la consulta de movimientos para ajustar el resultado al rango de fecha indicado en el
   // request entrante
   private Boolean force_transactions_date_filter = false;
   
   // Lista de nombres de canales para los cuales se envia a SB un numero de comprobante (provisto en la solicitud entrante)
   private Set<String> handle_receipt_on_channel = new HashSet<>();
   
   // Limite inferior que se aplica al rango de fechas solicitado en la consulta de movimientos.
   // Por ejemplo, si se asigna 2021-01-01, la consulta de movimientos no va a devolver movimientos mas antiguos a enero de 2021
   @DateTimeFormat(pattern = "yyyy-MM-dd")
   private LocalDate movements_limit_date = null;

   // Flag para forzar el uso de el metodo utilizado para bloqueo temporal de tarjetas
   // true: utiliza AppAuxWsaction/AWSCAMBIOESTADO_CUENTA.Execute
   // false: utiliza ConsultasVISAWebaction/ASISTARBANCMDF2.CAMBIOESTADOTARJETA
   private boolean use_block_card_alt_method = false;

   // Flag para forzar el estado devuelto en consulta de productos
   // true: si la tarjeta este bloqueada preventivamente por operaciones (codigo 98), se devuelve SUSPENDED_ON_REQUEST (como si hubiese sido bloqueada por el cliente)
   // false: si la tarjeta este bloqueada preventivamente por operaciones (codigo 98), se devuelve SUSPENDED_ACTIVE
   private boolean force_block_status = true;

   // Flag para habilitar la exclusion de autorizaciones de devolucion (devoluciones pendientes de aprobacion)
   // en la consulta de movimientos
   private boolean exclude_refund_authorizations = false;

   // Se utiliza para excluir movimientos en la consulta de movimientos por codigos SB o ITC.
   // Se espera que contenga expresiones como:
   //   code-<C>-scode-<S>[-N]
   //   code-<C>-scode-all[-N]
   //   itc-<XXX_XX>[-N]
   //   stat-<IS>-type-<IT>[-N]
   // donde:
   //   <C> y <S> corresponde a un codigo y subcodigo de movimiento SB
   //   <XXX_XX> es un codigo ITC, con un "_" substituyendo al "." (por ejemplo 100.02 -> 100_02)
   //   <IS> e <IT> corresponden a codigo de estado y codigo de typo de estado Infinitus
   //   <-N> Cuando se incluye el sufijo -N, el movimiento se excluye solo si tiene signo negativo
   private Set<String> exclude_movements = new HashSet<>();

   // Valor que se asigna al campo EstadoCodi cuando se solicita deshabilitar un parámetro de seguridad (método soap: ExceptionFraudeModi)
   private Integer security_exception_disable_code = 2;

   // Zona horaria utilizada en conversiones de String a Date/LocalDateTime y viceversa
   private String local_timezone = "America/Montevideo";

   // Cantidad de minutos que se adicioina a el campo "inicio" cuando se le solicita
   // una excepción de control de límites a API SB
   private Integer limit_control_start_offset_minutes = 0;

   // Número de sucursal enviado a SB en la solicitud de recarga.
   // Los valores indicados remplazan la sucursal provista en la solicitud recibida.
   private Map<String, Integer> force_branch_office = new HashMap<>();

   // Conjunto de expresiones que se van a evaluar al soliciatar un arecarga a SB por documento
   // para saber si es necesario retornar el numero de cuenta en la respuesta del SA.
   // Actualmente las expresiones soportados son del tipo:
   // ch-<canal> (ej.: ch-sar)
   private Set<String> return_acct_on_recharge = new HashSet<>();

   private Boolean enable_feature_umscr603 = true;

   // Offset por defecto aplicado a los campos inicio y fin, cuando se solicita deshabilitar control de límites,
   // en caso de que el valor no contenga offset
   private String from_field_default_offset = "-0000";

   // Afecta los datos que se envian en las solicitudes de alta de cuenta a API SB,
   // exclusivamente en los casos que se incluye informacion de representante (tipicamente
   // en altas de menores).
   // true: Se envía los datos del representante y el titular en la solicitud
   // false: Solo se envian los datos del titular, se ignoran los del representante
   private boolean include_representative_info = true;

   // Establecer timeouts

   private Integer force_read_timeout = 30000;
   private Integer force_connect_timeout = 30000;
   private Integer force_connection_request_timeout = 30000;

   private Map<String, String> err_code_by_root_exception = new HashMap<>();

   // Lista de expresiones regulares que se van a utilizar para obtener una representación corta del mensaje de error devuelto por SB.
   private List<String> result_message_patterns = new ArrayList<>();
   
   private String ctacicid_default_value = "10";
}
