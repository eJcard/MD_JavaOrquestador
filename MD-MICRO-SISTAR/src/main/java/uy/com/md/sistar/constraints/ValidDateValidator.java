package uy.com.md.sistar.constraints;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.common.base.Strings;

import uy.com.md.sistar.annotation.ValidDate;

public class ValidDateValidator implements ConstraintValidator<ValidDate, String> {

   private Boolean isOptional;

   @Override
   public void initialize(ValidDate validDate) {
      this.isOptional = validDate.optional();
   }

   @Override
   public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

      boolean validDate = isValidFormat("yyyy/MM/dd", value);

      return isOptional ? (validDate || (Strings.isNullOrEmpty(value))) : validDate;
   }

   private static boolean isValidFormat(String format, String value) {
      Date date = null;
      try {
         SimpleDateFormat sdf = new SimpleDateFormat(format);
         if (value != null) {
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
               date = null;
            }
         }

      } catch (ParseException ex) {
         ex.printStackTrace();
      }
      return date != null;
   }
}
