package uy.com.md.sistar.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum DocType {
   CI("1"),
   PA("2"),
   PU("3"),
   RU("4"),
   CE("5"),
   DI("6"),
   CC("7"),
   CU("8"),
   NI("9"),
   CA("10"),
   CN("11"),
   RC("12"),
   LE("13"),
   LC("14"),
   CB("15"),
   RP("16"),
   SA("17"),
   BP("18");

   public final String value;

   private static final Map<String, DocType> BY_VALUE = new HashMap<>();

   static {
      for (DocType e : values()) {
         BY_VALUE.put(e.value, e);
      }
   }

   DocType(String value) {
      this.value = value;
   }

   public static String getTypeByName(String name) {
      try{
         DocType docType = DocType.valueOf(name);
         return docType.value;
      }catch(IllegalArgumentException e){
         return null;
      }
   }

   public static String getType(String value) {
      final DocType dt = BY_VALUE.get(value);
      return Objects.isNull(dt) ? null : dt.name();
   }

}
