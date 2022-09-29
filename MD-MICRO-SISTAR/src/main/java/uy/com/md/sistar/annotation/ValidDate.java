package uy.com.md.sistar.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

import uy.com.md.sistar.constraints.ValidDateValidator;

/**
 * Validate that the annotated string is in YYYY-MM-DD Date format
 */

@Target(value = { PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = ValidDateValidator.class)
@Pattern(regexp = "^20\\d{2}-[0-1]\\d-[0-3]\\d$")
@Documented
public @interface ValidDate {

   String message() default "invalid date";

   Class<?>[] groups() default {};

   Class<? extends Payload>[] payload() default {};

   boolean optional() default false;

   String example() default "string";
}
