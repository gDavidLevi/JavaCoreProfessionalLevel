package ru.davidlevi.lesson7.homework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация Test помечает блок кода теста.
 * Имеет приоритет выполнения равный 5и.
 * - Компилируется и видна во время исполнения.
 * - Только для методов.
 *
 * @see TestMaker
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
    int priority() default 5;
}