package ru.davidlevi.conspects.lambda;

import java.util.function.Function;

public class MyFunction {
    private void method(Function<String, String> fl){
        fl.apply("......");
    }

    /* Точка входа */
    public static void main(String[] args) {
        /* Функция */
        Function<String, String> myFunc = (arg) -> {
            return arg + ", World!";
        };

        /* Вывод: Hello, World! */
        System.out.println(myFunc.apply("Hello"));

        MyFunction myFunction1 = new MyFunction();
        /* Функция высшего порядка (принимает на вход другую функцию) */
        myFunction1.method(myFunc);
    }
}
