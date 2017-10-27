package ru.davidlevi.lesson7.homework;

/**
 * Мой класс для тестирования
 */
public class MyTest {
    @BeforeSuite
    public static void startTest() {
        System.out.println("Start...");
    }

    /* Бросит исключение "RuntimeException: Аннотация @BeforeSuite дублируется" */
//    @BeforeSuite
//    public static void startTestD() {
//    }

    @Test(priority = -1)
    public static void step1() {
        System.out.println("test1() не попадет в тест");
    }

    @Test(priority = 1)
    public static void step2() {
        System.out.println(" priority = 1");
    }

    @Test(priority = 1)
    public static void step7() {
        System.out.println(" priority = 1");
    }

    @Test
    public static void step9() {
        System.out.println(" priority по умолчанию");
    }

    @Test(priority = 7)
    public static void step10() {
        System.out.println(" priority = 7");
    }

    @Test(priority = 10)
    public static void step5() {
        System.out.println(" priority = 10");
    }

    @Test(priority = 3)
    public static void step3() {
        System.out.println(" priority = 3");
    }

    @Test(priority = 5)
    public static void step4() {
        System.out.println(" priority = 5");
    }

    @Test(priority = 11)
    public static void step6() {
        System.out.println("не должен попасть в тест");
    }

    @AfterSuite
    public static void endTest() {
        System.out.println("...stop.");
    }
}