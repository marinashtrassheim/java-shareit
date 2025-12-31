package ru.practicum.shareit.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EndAfterStartServerValidator.class)
@Documented
public @interface EndAfterStartServer {
    String message() default "Дата окончания должна быть после даты начала";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}