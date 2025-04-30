package com.group7.krisefikser.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * Custom validator for validating that a string value is a valid enum constant.
 * The enum class must be specified in the annotation.
 * The validation is case-insensitive.
 */
public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

  private List<String> enumValuesLowercase;

  @Override
  public void initialize(ValidEnum constraintAnnotation) {
    Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
    enumValuesLowercase = Arrays.stream(enumClass.getEnumConstants())
            .map(Enum::name)
            .map(String::toLowerCase)
            .toList();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    return enumValuesLowercase.contains(value.toLowerCase());
  }
}