package com.epam.rd.autocode.spring.project.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderModelTest {
    private static List<Field> allFields;
    private static List<Constructor<?>> allConstructors;

    @BeforeAll
    static void setup() throws ClassNotFoundException {
        final Class<?> clazz = Class.forName(Constants.ORDER_TYPE);
        allFields = Arrays.asList(clazz.getDeclaredFields());
        allConstructors = Arrays.asList(clazz.getConstructors());
    }

    @Test
    @DisplayName("Count constructors")
    void checkCountConstructors() {
        assertEquals(Constants.Order.CLASS_COUNT_CONSTRUCTORS, allConstructors.size());
    }

    @Test
    @DisplayName("Modifiers constructors can be public")
    void checkModifiersConstructors() {
        var actual = allConstructors.stream()
                .allMatch(constructor -> Modifier.isPublic(constructor.getModifiers()));

        assertTrue(actual);
    }

    @Test
    @DisplayName(Constants.Order.CLASS_NAME + " has default constructor")
    void checkDefaultConstructor() {
        var count = allConstructors.stream()
                .filter(constructor -> constructor.getParameterCount() == 0)
                .count();

        assertEquals(1, count);
    }

    @Test
    @DisplayName(Constants.Order.CLASS_NAME + " has constructor with " + Constants.Order.PARAMETERS_IN_CONSTRUCTOR_WITH_PARAMETERS + " parameters")
    void checkConstructorWithParameter() {
        var count = allConstructors.stream()
                .filter(constructor -> constructor.getParameterCount() == Constants.Order.PARAMETERS_IN_CONSTRUCTOR_WITH_PARAMETERS)
                .count();

        assertEquals(1, count);
    }

    @Test
    @DisplayName("Check parameter type in constructor with parameter")
    void checkParameterTypeForConstructorWithParameter() {
        final var constructor = allConstructors.stream()
                .filter(c -> c.getParameterCount() == Constants.Order.PARAMETERS_IN_CONSTRUCTOR_WITH_PARAMETERS)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No constructor with parameters"));

        final var parameters = Arrays.asList(constructor.getParameters());

        parameters.stream()
                .filter(p -> p.getType().getTypeName().equals(Constants.LONG_TYPE))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No parameter with type " + Constants.LONG_TYPE));

        parameters.stream()
                .filter(p -> p.getType().getTypeName().equals(Constants.CLIENT_TYPE))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No parameter with type " + Constants.CLIENT_TYPE));

        parameters.stream()
                .filter(p -> p.getType().getTypeName().equals(Constants.EMPLOYEE_TYPE))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No parameter with type " + Constants.EMPLOYEE_TYPE));

        parameters.stream()
                .filter(p -> p.getType().getTypeName().equals(Constants.LOCAL_DATE_TIME_TYPE))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No parameter with type " + Constants.LOCAL_DATE_TIME_TYPE));

        parameters.stream()
                .filter(p -> p.getType().getTypeName().equals(Constants.BIG_DECIMAL_TYPE))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No parameter with type " + Constants.BIG_DECIMAL_TYPE));

        assertEquals(Constants.Order.PARAMETERS_IN_CONSTRUCTOR_WITH_PARAMETERS, parameters.size());
    }

    @Test
    @DisplayName("Count fields")
    void checkCountFields() {
        assertEquals(Constants.Order.CLASS_COUNT_FIELDS, allFields.size());
    }

    @Test
    @DisplayName("Modifiers fields can be private")
    void checkModifiersFields() {
        var count = allFields.stream()
                .filter(f -> Modifier.isPrivate(f.getModifiers()))
                .count();

        assertEquals(Constants.Order.CLASS_COUNT_FIELDS, count);
    }

    @DisplayName("Check field type and field name")
    @ParameterizedTest
    @CsvSource({
            "java.lang.Long, id, 1",
            "com.epam.rd.autocode.spring.project.model.Client, client, 1",
            "com.epam.rd.autocode.spring.project.model.Employee, employee, 1",
            "java.time.LocalDateTime, orderDate, 1",
            "java.math.BigDecimal, price, 1",
            "java.util.List, bookItems, 1"
    })
    void checkNameFieldType(String fieldType, String fieldName, long expected) {
        var count = allFields.stream()
                .filter(f -> f.getType().getTypeName().equals(fieldType)
                        & f.getName().equals(fieldName))
                .count();
        assertEquals(expected, count);
    }
}
