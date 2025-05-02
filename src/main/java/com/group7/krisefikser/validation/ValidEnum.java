package com.group7.krisefikser.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for validating that a string value is a valid enum constant.
 * The enum class must be specified in the annotation.
 * The validation is case-insensitive.
 */
@Documented
@Constraint(validatedBy = EnumValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEnum {
  /**
   * The enum class that the string value must match.
   */
  Class<? extends Enum<?>> enumClass();

  /**
   * The error message to be returned if the validation fails.
   */
  String message() default "must be a valid enum value from {enumClass}";

  /**
   * The groups the constraint belongs to.
   */
  Class<?>[] groups() default {};

  /**
   * Additional data that can be used by the validation framework.
   */
  Class<? extends Payload>[] payload() default {};
}