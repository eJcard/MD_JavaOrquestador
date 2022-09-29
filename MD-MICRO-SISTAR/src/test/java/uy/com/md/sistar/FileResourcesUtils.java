package uy.com.md.sistar;

import java.io.File;
import java.net.URI;

import org.springframework.util.ResourceUtils;

import lombok.SneakyThrows;

public class FileResourcesUtils {

   private FileResourcesUtils() {

   }

   @SneakyThrows
   public static File[] filesFrom(String relativeDirectorysPath) {
      final URI uri = ResourceUtils.getURL("classpath:" + relativeDirectorysPath).toURI();
      final File directory = new File(uri);
      return directory.listFiles();
   }

   @SneakyThrows
   public static File[] filesFrom(String relativeDirectorysPath, String fileExtension) {
      final URI uri = ResourceUtils.getURL("classpath:" + relativeDirectorysPath).toURI();
      final File directory = new File(uri);
      return directory.listFiles((File file) -> file.getName().toLowerCase().endsWith(fileExtension));
   }
}
