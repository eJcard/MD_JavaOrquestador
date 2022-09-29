package uy.com.md.sistar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import mockit.Mock;
import mockit.MockUp;
import uy.com.md.sistar.dto.IdentificationDto;
import uy.com.md.sistar.dto.InfoAdicionalDto;
import uy.com.md.sistar.dto.in.NewCardRequestDto;
import uy.com.md.sistar.dto.in.ReverseRequestDto;
import uy.com.md.sistar.dto.out.BeneficiarioDto;
import uy.com.md.sistar.dto.out.DireccionDto;
import uy.com.md.sistar.dto.out.DocDto;
import uy.com.md.sistar.dto.out.TelefonoDto;
import uy.com.md.sistar.dto.out.TransactionInfo;
import uy.com.md.sistar.soap.client.AccountDTO;
import uy.com.md.sistar.soap.client.CustomerDTO;
import uy.com.md.sistar.soap.client.MOVENPERIODO;
import uy.com.md.sistar.soap.client.MOVENPERIODOITEM;
import uy.com.md.sistar.soap.client.SistarbancMDF2ABMCUENTATARJETA;
import uy.com.md.sistar.soap.client.SistarbancMDV2MOVENPERIODOResponse;
import uy.com.md.sistar.soap.client.WSPagosAjustes;
import uy.com.md.sistar.util.ExternalData;

@SpringBootTest
@ActiveProfiles("test")
public class ModelMapperConfigTest {

   @Autowired
   private ModelMapper mapper;

   private static final NewCardRequestDto inRequest = new NewCardRequestDto();

   private static final ReverseRequestDto inCreditReverse = new ReverseRequestDto();

   @BeforeAll
   public static void setUp() {
      createNewCardRequestDto();
      createReverseRequestDto();

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
   public void testRequestMovementsMapping() throws DatatypeConfigurationException {
      SistarbancMDV2MOVENPERIODOResponse resp = new SistarbancMDV2MOVENPERIODOResponse();
      resp.setMovenperiodo(new MOVENPERIODO());
      MOVENPERIODOITEM item = new MOVENPERIODOITEM();
      resp.getMovenperiodo().getMOVENPERIODOITEM().add(item);

      item.setACCOUNTNUMBER("12345");
      item.setAUTHORIZATIONCODE("AUTH123");
      item.setORIGINALAMOUNT(10);
      item.setORIGINALCURRENCY((short)840);
      item.setSETTLEMENTAMOUNT(450);
      item.setSETTLEMENTCURRENCY((short)858);
      item.setCARDNUMBER("1234567891234567");
      item.setMERCHANTNAME("REDPAGOS");
      item.setESTADOCODI((short)60);
      item.setESTADOTIPO((short)6);
      item.setTRANSACTIONDATE(DatatypeFactory.newInstance().newXMLGregorianCalendar(2020, 1, 22, 0, 0, 0, 0, 0));

      TransactionInfo obj = mapper.map(item, TransactionInfo.class);

      assertEquals(item.getSETTLEMENTAMOUNT(), obj.getAmount().doubleValue());
      assertEquals(String.valueOf(item.getSETTLEMENTCURRENCY()), obj.getCurrency());
      assertEquals(item.getORIGINALAMOUNT(), obj.getOriginalAmount().doubleValue());
      assertEquals(String.valueOf(item.getORIGINALCURRENCY()), obj.getOriginalCurrency());
      assertEquals(item.getACCOUNTNUMBER(), obj.getAdditionalData().getRequester().getAccount());
      assertEquals(item.getAUTHORIZATIONCODE(), obj.getApprovalCode());
      assertEquals(String.valueOf(item.getMCC()), obj.getMcc());
      assertEquals(item.getMERCHANTNAME(), obj.getMerchantDescription());
      assertTrue(obj.getConfirmed());
   }

   @Test
   public void testCreateCardMapping() {
      SistarbancMDF2ABMCUENTATARJETA outRequest = mapper.map(inRequest, SistarbancMDF2ABMCUENTATARJETA.class);
      AccountDTO account = outRequest.getAccount();

      List<CustomerDTO> customersDto = account.getPrincipalCustomer().getCustomerDTO();
      assertEquals(1, customersDto.size());
      if (customersDto.size() == 1) {
         CustomerDTO mainCustomer = outRequest.getAccount().getPrincipalCustomer().getCustomerDTO().get(0);

         assertEquals("GUSTAVO", mainCustomer.getNombre(), "Nombre incorrecto");
         assertEquals("RAMIRO", mainCustomer.getNombre2(), "Segundo nombre incorrecto");
         assertEquals("FERNANDEZ", mainCustomer.getApellido(), "Apellido incorrecto");
         assertEquals("MARTINEZ", mainCustomer.getApellido2(), "Segundo apellido incorrecto");
         assertEquals("a@a.com", mainCustomer.getEMail(), "Email incorrecto");
         assertEquals("19830818", mainCustomer.getBirthday(), "Fecha de nacimiento incorrecta");
         assertEquals("858", mainCustomer.getPaIdNacCustomer(), "Nacionalidad incorrecta");
         assertEquals("A", account.getActId(), "Estado de actividad incorrecta");
         assertEquals("123", account.getCodActId(), "Codigo de actividad incorrecto");
         assertEquals("M", mainCustomer.getCodSex(), "Sexo incorrecto");
         assertEquals("m", mainCustomer.getCivId(), "Estado civil incorrecto");
         assertEquals("T", mainCustomer.getTrjRel(), "Tipo de tarjeta (principal / adicional) incorrecto");

         // Producto
         assertEquals(100, mainCustomer.getProduct().getCreditLimitPorce(), "% de limite de credito incorrecto");
         assertEquals("1", mainCustomer.getProduct().getPreId(), "Codigo de precio incorrecto");
         assertEquals("S", mainCustomer.getProduct().getRenovAutom(), "Flag de renovacion automatica invorrecto");
         assertEquals(1, mainCustomer.getProduct().getAfinityGroupCode(), "Grupo de afinidad incorrecto");
         assertEquals(42, mainCustomer.getProduct().getProductCode(), "Codigo de producto incorrecto");
         assertEquals("1", account.getBrandCode(), "Codigo de marca incorrecto");

         // Documento
         assertEquals("CI", mainCustomer.getDocumentType(), "Tipo de documento incorrecto");
         assertEquals("858", mainCustomer.getPaIdDocCustomer(), "Codigo de pais incorreecto");
         assertEquals("10000008", mainCustomer.getDocumentNumber(), "Numero de doc. incorrecto");
         assertEquals("CI", account.getDocumentTypeT(), "Tipo de documento incorrecto");
         assertEquals("10000008", account.getDocumentNumberT(), "Numero de doc. incorrecto");

         // Direccion
         assertEquals("Solomeo Paredes 1234 ap. 1000 piso 10", mainCustomer.getPhisicalAddress().getAddress(), "Direccion incorrecta");
         assertEquals("S", mainCustomer.getPhisicalAddress().getResidente(), "Residencia incorrecta");
         assertEquals("10000", mainCustomer.getPhisicalAddress().getZipCode(), "Codigo postal invalido");
         assertEquals("1", mainCustomer.getPhisicalAddress().getCityCode(), "Codigo de ciudad incorrecto");
         assertEquals("1", mainCustomer.getPhisicalAddress().getGeoStateCode(), "Codigo de departamento incorrecto");
         assertEquals("858", mainCustomer.getPhisicalAddress().getCountryCode(), "Codigo de pais incorrecto");

         // Telefonos
         assertEquals("21231234", mainCustomer.getPhoneNumber(), "Numero de telefono fijo incorrecto");
         assertEquals("094123123", mainCustomer.getCellNumber(), "Numero de celular incorrecto");

         // Parametros de la solicitud
         assertEquals("Midinero", outRequest.getUsuariows(), "valor incorrecto en Usuariows");
         assertEquals("gld", outRequest.getUsuario(), "valor incorrecto en Usuario");
         assertEquals("amEnione", outRequest.getPassws(), "valor incorrecto en Passws");
         assertEquals("A", outRequest.getOperationcode(), "valor incorrecto en OperationCode");

         // Sucursales
         assertEquals("21", account.getCtaBcoSuc(), "Codigo de sucursal solicitante incorrecto");
         assertEquals("20", account.getCtaBcoSuc2(), "Codigo de sucursal de entrega incorrecto");

         // Emisor
         assertEquals("67", account.getBcoId(), "Identificador del banco/emisor");

         // Otros
         assertEquals("N", account.getOperaPorTerceros(), "Flag de operacion por cuenta de terceros incorrecto");
         assertEquals("FERNANDEZ GUSTAVO", mainCustomer.getNombreEmbozado(), "Nombre de embosado incorrecto");
         assertEquals("P", account.getCtaTpo(), "Tipo de cuenta incorrecto");
         assertEquals("1", account.getCtaCicId(), "Codigo de ciclo incorrecto");
         assertEquals("0001", account.getCtaCraId(), "Codigo de cartera incorrecto");
         assertEquals("858", account.getPaiid(), "Pais de residencia incorrecto");
         assertEquals("NI", account.getEnvId(), "Codigo de envio incorrecto");
      }
   }
   
   
   @Test
   public void testMappingReverseCredit() {
	   WSPagosAjustes requestSoap = mapper.map(inCreditReverse, WSPagosAjustes.class);
      
      if (requestSoap != null) {
         assertEquals("R", requestSoap.getTipoWs(), "Tipo de operacion incorrecta");
        
         assertEquals(Long.valueOf("360100000114"), requestSoap.getCuentaTarjeta(), "Cuenta de tarjeta incorrecta");
         assertEquals("00000004", requestSoap.getIdReferencia(), "Id de referencia incorrecto");
         assertEquals("Su deposito", requestSoap.getDescripcion(), "Descripcion incorrecta");
      }
   }
   
   
   private static void createNewCardRequestDto() {
		BeneficiarioDto beneficiary = new BeneficiarioDto();
	      beneficiary.setNombre("Gustavo");
	      beneficiary.setNombre2("Ramiro");
	      beneficiary.setActividad("123123");
	      beneficiary.setApellido("Fernandez");
	      beneficiary.setApellido2("Martinez");
	      beneficiary.setEmail("a@a.com");
	      beneficiary.setEstadoCivil("m");
	      beneficiary.setFechaNacimiento("19830818");
	      beneficiary.setNacionalidad("858");
	      beneficiary.setSexo("MAS");

	      DireccionDto address = new DireccionDto();
	      address.setApto("1000");
	      address.setCalle("Solomeo Paredes");
	      address.setPuerta("1234");
	      address.setPiso("10");
	      address.setCodigoPostal("10000");
	      address.setResidente(true);
	      address.setDepartamento(1);
	      address.setLocalidad(20);
	      address.setBarrio(38);
	      address.setPais("858");
	      address.setAmplicacionCalle("Redpagos Central");
	      beneficiary.setDireccion(address);

	      DocDto doc = new DocDto();
	      doc.setNum("10000008");
	      doc.setPais("858");
	      doc.setTipoDoc("1");
	      beneficiary.setDoc(doc);

	      List<TelefonoDto> telefonos = new ArrayList<>();
	      beneficiary.setTelefonos(telefonos);

	      TelefonoDto telefono = new TelefonoDto();
	      telefono.setNum("21231234");
	      telefono.setTipoTel(2);
	      beneficiary.getTelefonos().add(telefono);
	      telefono = new TelefonoDto();
	      telefono.setNum("094123123");
	      telefono.setTipoTel(1);
	      beneficiary.getTelefonos().add(telefono);

	      inRequest.setBeneficiario(beneficiary);
	      inRequest.setProducto("042");
	      inRequest.setEmisor("67");
	      inRequest.setGrupoAfinidad("1");
	      inRequest.setRed("1");
	      inRequest.setSucursal("21");
	      inRequest.setSucursalEntrega("20");
	      inRequest.setOperaPorTerceros(false);
	      inRequest.setMarca(1L);
	}
   
   private static void createReverseRequestDto() {
	   	IdentificationDto empresa = new IdentificationDto();
	   	empresa.setTipoDoc("4");
        empresa.setPais("858");
        empresa.setDoc("123412341234");
        empresa.setNombre("Casa de cambio");
	   	
		IdentificationDto ordenante = new IdentificationDto();
	   	ordenante.setTipoDoc("1");
        ordenante.setPais("858");
        ordenante.setDoc("1234123");
        ordenante.setNombre("Juan Perez");
		
		InfoAdicionalDto infoAdicional = new InfoAdicionalDto();

		inCreditReverse.setEmisor(36);
		inCreditReverse.setRrn("00000004");
		inCreditReverse.setToReverseRrn("0000005");
   }
}
