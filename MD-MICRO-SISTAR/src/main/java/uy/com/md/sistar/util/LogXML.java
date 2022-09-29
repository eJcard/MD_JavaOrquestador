package uy.com.md.sistar.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogXML<T> {

   private static final Logger logger = LoggerFactory.getLogger(LogXML.class);

   final Class<T> typeParameterClass;

   public LogXML(Class<T> typeParameterClass) {
      this.typeParameterClass = typeParameterClass;
   }

   public void log(T object) {
      try {
         JAXBContext jc = JAXBContext.newInstance(typeParameterClass);
         Marshaller marshaller = jc.createMarshaller();
         marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         marshaller.marshal(object, output);
         String xml = output.toString("UTF-8");
         logger.info(xml);
      } catch (JAXBException | UnsupportedEncodingException e) {
         logger.error("Fail to log XML", e);
      }
   }
}
