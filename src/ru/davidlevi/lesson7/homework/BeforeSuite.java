package ru.davidlevi.lesson7.homework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация BeforeSuite помечает блок кода до тестов.
 * Должна быть выполнена 1 раз в тесте иначе бросается исключение RuntimeException.
 * - Компилируется и видна во время исполнения.
 * - Только для методов.
 *
 * @see TestMaker
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface BeforeSuite {

}