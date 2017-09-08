package ru.davidlevy.lesson7.homework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс обрабатывающий тесты
 */
final class TestMaker {
    /**
     * Запуск тестирования класса
     *
     * @param testClass класс для тестирования
     */
    public static void start(Class testClass) {
        try {
            testing(testClass);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Запуск тестирования класса (перегруженный метод)
     *
     * @param classFullName полное наименование класса
     */
    public static void start(final String classFullName) {
        try {
            testing(Class.forName(classFullName));
        } catch (InvocationTargetException | InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Тестирование
     *
     * @param testClass тестируемый класс
     * @throws InvocationTargetException ошибка
     * @throws IllegalAccessException    ошибка
     * @throws InstantiationException    ошибка
     */
    private static void testing(Class testClass) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        /* Используя Reflection API получаем из testClass
        массив всех объявленных в нём методов */
        Method[] declaredMethods = testClass.getDeclaredMethods();

        /* Список для методов, которые будут использоваться в тестировании */
        List<Method> invokeMethods = new ArrayList<>();

        System.out.printf("Тестирование класса %s%n", testClass.getName());

        /* Обработка аннотаций.
        Заполнение списка invokeMethods по приоритетам */
        Method beforeSuite = null;
        Method afterSuite = null;
        for (Method oneOf : declaredMethods) {
            if (oneOf.getDeclaredAnnotations().length == 0)
                continue;
            /* Если обнаружен дубль @BeforeSuite */
            if (oneOf.isAnnotationPresent(BeforeSuite.class)) {
                if (beforeSuite != null)
                    throw new RuntimeException("Аннотация @BeforeSuite дублируется в классе.");
                beforeSuite = oneOf;
                continue;
            }
            /* Если обнаружен дубль @AfterSuite */
            if (oneOf.isAnnotationPresent(AfterSuite.class)) {
                if (afterSuite != null)
                    throw new RuntimeException("Аннотация @AfterSuite дублируется в классе.");
                afterSuite = oneOf;
                continue;
            }
            /* Добавим в список invokeMethods только те методы,
            у которых приоритет лежит в диапазоне от 1 до 10 */
            if (oneOf.isAnnotationPresent(Test.class)) {
                int priority = oneOf.getAnnotation(Test.class).priority();
                if (priority <= 10 & priority >= 1)
                    invokeMethods.add(oneOf);
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
        if (beforeSuite != null)
            invokeMethods.add(0, beforeSuite);

        /* Добавим в конец списка invokeMethods @AfterSuite */
        if (afterSuite != null)
            invokeMethods.add(afterSuite);

        /* Создаем экземпляр класса для запуска методов */
        Object object = testClass.newInstance();

        /* Выполнить все тесты */
        for (Method oneOf : invokeMethods)
            oneOf.invoke(object); // Можно с null
    }
}