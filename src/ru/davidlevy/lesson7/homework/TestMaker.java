package ru.davidlevy.lesson7.homework;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Класс обрабатывающий тесты
 */
final class TestMaker {
    static void start(Class testClass) throws Exception {
        /* Используя Reflection API получаем из testClass
        массив всех объявленных в нём методов */
        Method[] declaredMethods = testClass.getDeclaredMethods();

        /* Список для методов, которые будут использоваться в тестировании */
        List<Method> invokeMethods = new ArrayList<>();

        System.out.printf("Тестирование класса %s%n", testClass.getName());

        /* Обработка аннотаций.
        Заполнение списка invokeMethods по приоритетам */
        boolean isOneBeforeSuite = false;
        boolean isOneAfterSuite = false;
        for (Method test : declaredMethods) {
            /* Если обнаружен дубль @BeforeSuite */
            if (test.isAnnotationPresent(BeforeSuite.class)) {
                if (isOneBeforeSuite)
                    throw new RuntimeException("Аннотация @BeforeSuite дублируется.");
                isOneBeforeSuite = true;
            }
            /* Если обнаружен дубль @AfterSuite */
            if (test.isAnnotationPresent(AfterSuite.class)) {
                if (isOneAfterSuite)
                    throw new RuntimeException("Аннотация @AfterSuite дублируется.");
                isOneAfterSuite = true;
            }
            /* Добавим в список invokeMethods только те методы,
            у которых приоритет лежит в диапазоне от 1 до 10 */
            if (test.isAnnotationPresent(Test.class)) {
                int priority = test.getAnnotation(Test.class).priority();
                if (priority <= 10 & priority >= 1)
                    invokeMethods.add(test);
            }
        }

        /* Cортируем список в обратном порядке, чтобы
        первым выполнялся метод с наивысшим приоритетом */
        invokeMethods.sort((method1, method2) -> {
            Integer priority1 = method1.getAnnotation(Test.class).priority();
            Integer priority2 = method2.getAnnotation(Test.class).priority();
            return Integer.compare(priority2, priority1);
        });

        /* Добавим в начала списка invokeMethods @BeforeSuite */
        for (Method method : declaredMethods)
            if (method.isAnnotationPresent(BeforeSuite.class))
                invokeMethods.add(0, method);

        /* Добавим в конец списка invokeMethods @AfterSuite */
        for (Method method : declaredMethods)
            if (method.isAnnotationPresent(AfterSuite.class))
                invokeMethods.add(method);

        /* Выполним все тесты из списка invokeMethods */
        for (Method oneOf : invokeMethods) {
            //System.out.print(new SimpleDateFormat("HH:mm:ss:S").format(Calendar.getInstance().getTime()) + " ");
            oneOf.invoke(null);
        }
    }
}