package uy.com.md.sistar;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;

import mockit.Mock;
import mockit.MockUp;
import uy.com.md.sistar.dto.in.NewCardRequestDto;
import uy.com.md.sistar.soap.client.CrearTarjetaPrepagaInnominada;
import uy.com.md.sistar.util.ExternalData;
import uy.com.md.sistar.util.ExternalIdUtils;

@SpringBootTest
@ActiveProfiles("test")
public class InnominadaTest extends BaseClientTest {

   private static NewCardRequestDto jsonRequest;

   private static String xmlRequestControl;

   @Autowired
   private ModelMapper modelMapper;

   @BeforeAll
   public static void setUp() throws IOException {
      OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      jsonRequest = OBJECT_MAPPER.readValue(getRequestFile("tarjetaInnominadaRequest.json"), NewCardRequestDto.class);
      xmlRequestControl = getRequestFileContent("tarjetaInnominadaRequest.xml");

      new MockUp<ExternalIdUtils>() {

         @Mock
         String getIdAsString(int maxWidth) {
            return "123456";
         }
      };

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
   @Disabled
   public void innominadaMappingOK() throws JAXBException, UnsupportedEncodingException {

      CrearTarjetaPrepagaInnominada requestSoap = modelMapper.map(jsonRequest, CrearTarjetaPrepagaInnominada.class);
      assertNotNull(requestSoap, "Request cannot be null");

      JAXBContext jc = JAXBContext.newInstance(CrearTarjetaPrepagaInnominada.class);
      Marshaller marshaller = jc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      marshaller.marshal(requestSoap, output);
      String xml = output.toString("UTF-8");

      Diff myDiff = DiffBuilder.compare(xmlRequestControl).withTest(xml).checkForSimilar().build();
      assertFalse(myDiff.toString(), myDiff.hasDifferences());

   }

}
