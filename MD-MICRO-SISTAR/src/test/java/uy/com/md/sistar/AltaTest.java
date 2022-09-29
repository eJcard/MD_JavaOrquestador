package uy.com.md.sistar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;

import mockit.Mock;
import mockit.MockUp;
import uy.com.md.sistar.dto.in.NewCardRequestDto;
import uy.com.md.sistar.soap.SistarClient;
import uy.com.md.sistar.soap.client.AccountDTO;
import uy.com.md.sistar.soap.client.CustomerDTO;
import uy.com.md.sistar.soap.client.SistarbancMDF2ABMCUENTATARJETA;
import uy.com.md.sistar.util.ExternalData;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AltaTest extends BaseClientTest {

   private static NewCardRequestDto cardRequestDto;

   @MockBean(name = "SistarClientMDF2")
   private SistarClient sistarClient;

   @Autowired
   private TestRestTemplate restTemplate;

   @Autowired
   private ModelMapper modelMapper;

   @BeforeAll
   public static void setUp() throws IOException {
      OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      cardRequestDto = OBJECT_MAPPER.readValue(getRequestFile("solicitudTarjetaNominadaRequest.json"), NewCardRequestDto.class);

      new MockUp<ExternalData>() {

         @Mock
         String getCodLocalidad(Integer idLocalidad, Integer codDepartamento, String codPais) {
            return "1";
         }

         @Mock
         String getCodDepartamento(Integer idDepartamento, String codPais) {
            return "1";
         }
      };
   }

   @Test
   public void requestMapperOK() {
      SistarbancMDF2ABMCUENTATARJETA requestSoap = modelMapper.map(cardRequestDto, SistarbancMDF2ABMCUENTATARJETA.class);
      testMapping(requestSoap);
   }

   private void testMapping(SistarbancMDF2ABMCUENTATARJETA requestSoap) {
      assertNotNull(requestSoap);
      assertNotNull(requestSoap.getAccount());
      assertNotNull(requestSoap.getAccount().getPrincipalCustomer());
      assertNotNull(requestSoap.getAccount().getPrincipalCustomer().getCustomerDTO());
      assertEquals(1, requestSoap.getAccount().getPrincipalCustomer().getCustomerDTO().size());

      AccountDTO account = requestSoap.getAccount();
      CustomerDTO mainCustomer = requestSoap.getAccount().getPrincipalCustomer().getCustomerDTO().get(0);

      assertEquals("EDUARDO", mainCustomer.getNombre(), "Nombre incorrecto");
      assertEquals("ANDRES", mainCustomer.getNombre2(), "Segundo nombre incorrecto");
      assertEquals("CARBALLIDO", mainCustomer.getApellido(), "Apellido incorrecto");
      assertEquals("LORENZOTTI", mainCustomer.getApellido2(), "Segundo apellido incorrecto");
      assertEquals("eduardo.carballido@atos.net", mainCustomer.getEMail(), "Email incorrecto");
      assertEquals("19750104", mainCustomer.getBirthday(), "Fecha de nacimiento incorrecta");
      assertEquals("858", mainCustomer.getPaIdNacCustomer(), "Nacionalidad incorrecta");
      assertEquals("I", account.getActId(), "Estado de actividad incorrecta");
      assertEquals("345", account.getCodActId(), "Codigo de actividad incorrecto");
      assertEquals("M", mainCustomer.getCodSex(), "Sexo incorrecto");
      assertEquals("S", mainCustomer.getCivId(), "Estado civil incorrecto");
      assertEquals("T", mainCustomer.getTrjRel(), "Tipo de tarjeta (principal / adicional) incorrecto");

      // Producto
      assertEquals(100, mainCustomer.getProduct().getCreditLimitPorce(), "% de limite de credito incorrecto");
      assertEquals("1", mainCustomer.getProduct().getPreId(), "Codigo de precio incorrecto");
      assertEquals("S", mainCustomer.getProduct().getRenovAutom(), "Flag de renovacion automatica invorrecto");
      assertEquals(1, mainCustomer.getProduct().getAfinityGroupCode(), "Grupo de afinidad incorrecto");
      assertEquals(42, mainCustomer.getProduct().getProductCode(), "Codigo de producto incorrecto");
      //         assertEquals("1", account.getBrandCode(), "Codigo de marca incorrecto");

      // Documento
      assertEquals("CI", mainCustomer.getDocumentType(), "Tipo de documento incorrecto");
      assertEquals("858", mainCustomer.getPaIdDocCustomer(), "Codigo de pais incorreecto");
      assertEquals("10000008", mainCustomer.getDocumentNumber(), "Numero de doc. incorrecto");
      assertEquals("CI", account.getDocumentTypeT(), "Tipo de documento incorrecto");
      assertEquals("10000008", account.getDocumentNumberT(), "Numero de doc. incorrecto");

      // Direccion
      assertEquals("Solomeo Paredes 1234 A ap. 1000 piso 10", mainCustomer.getPhisicalAddress().getAddress(), "Direccion incorrecta");
      assertEquals("S", mainCustomer.getPhisicalAddress().getResidente(), "Residencia incorrecta");
      assertEquals("11600", mainCustomer.getPhisicalAddress().getZipCode(), "Codigo postal invalido");
      assertEquals("1", mainCustomer.getPhisicalAddress().getCityCode(), "Codigo de ciudad incorrecto");
      assertEquals("1", mainCustomer.getPhisicalAddress().getGeoStateCode(), "Codigo de departamento incorrecto");
      assertEquals("858", mainCustomer.getPhisicalAddress().getCountryCode(), "Codigo de pais incorrecto");

      // Telefonos
      assertEquals("23004005", mainCustomer.getPhoneNumber(), "Numero de telefono fijo incorrecto");
      assertEquals("099189167", mainCustomer.getCellNumber(), "Numero de celular incorrecto");

      // Parametros de la solicitud
      assertEquals("Midinero", requestSoap.getUsuariows(), "valor incorrecto en Usuariows");
      assertEquals("gld", requestSoap.getUsuario(), "valor incorrecto en Usuario");
      assertEquals("amEnione", requestSoap.getPassws(), "valor incorrecto en Passws");
      assertEquals("A", requestSoap.getOperationcode(), "valor incorrecto en OperationCode");

      // Sucursales
      assertEquals("1", account.getCtaBcoSuc(), "Codigo de sucursal solicitante incorrecto");
      assertEquals("20", account.getCtaBcoSuc2(), "Codigo de sucursal de entrega incorrecto");

      // Emisor
      assertEquals("36", account.getBcoId(), "Identificador del banco/emisor");

      // Otros
      assertEquals("N", account.getOperaPorTerceros(), "Flag de operacion por cuenta de terceros incorrecto");
      assertEquals("CARBALLIDO, EDUARDO", mainCustomer.getNombreEmbozado(), "Nombre de embosado incorrecto");
      assertEquals("P", account.getCtaTpo(), "Tipo de cuenta incorrecto");
      assertEquals("1", account.getCtaCicId(), "Codigo de ciclo incorrecto");
      assertEquals("0001", account.getCtaCraId(), "Codigo de cartera incorrecto");
      assertEquals("858", account.getPaiid(), "Pais de residencia incorrecto");
      assertEquals("NI", account.getEnvId(), "Codigo de envio incorrecto");
   }
}
