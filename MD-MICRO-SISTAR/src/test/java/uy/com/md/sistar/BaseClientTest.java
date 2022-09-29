package uy.com.md.sistar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseClientTest {

   protected static final String BASE_PATH = "sistar";

   protected static final String PATH_SOL_NOMINADA = "/" + BASE_PATH + "/solicitud-producto-nominado";

   private static final String JSON_EXTENSION = "json";

   private static final String CONTENT_TYPE = "application/json";

   private static final String BASE_TEST_REQUEST_RESOURCES = "requests";

   private static final String BASE_TEST_RESPONSE_RESOURCES = "responses";

   protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

   protected static File[] responsesFiles;

   protected static File[] requestsFiles;

   public static File getRequestFile(String fileName) {
      if (Objects.isNull(requestsFiles)) {
         getRequestsFiles();
      }
      for (File file : requestsFiles) {
         if (file.getName().equals(fileName)) {
            return file;
         }
      }
      return null;
   }

   public static File getResponseFile(String fileName) {
      if (Objects.isNull(responsesFiles)) {
         getResponsesFiles();
      }
      for (File file : responsesFiles) {
         if (file.getName().equals(fileName)) {
            return file;
         }
      }
      return null;
   }

   private static void getRequestsFiles() {
      requestsFiles = FileResourcesUtils.filesFrom(BASE_TEST_REQUEST_RESOURCES);
   }

   private static void getResponsesFiles() {
      responsesFiles = FileResourcesUtils.filesFrom(BASE_TEST_RESPONSE_RESOURCES);
   }

   public static String getRequestFileContent(String fileName) {
      String ret;
      try {
         ret = new String(Files.readAllBytes(Objects.requireNonNull(getRequestFile(fileName)).toPath()));
      } catch (IOException e) {
         ret = "";
      }
      return ret;
   }
}
