package uy.com.md.sistar.util;

import java.time.Clock;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class ExternalIdUtils {

   public static Long getIdAsLong(int maxWidth) {
      return NumberUtils.toLong(getIdAsString(maxWidth));
   }

   public static String getIdAsString(int maxWidth) {
      final String ref = Long.toString(Clock.systemDefaultZone().millis());
      return StringUtils.truncate(ref, maxWidth);
   }

}
