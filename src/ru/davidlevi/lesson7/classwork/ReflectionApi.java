package ru.davidlevi.lesson7.classwork;

import java.lang.annotation.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class ReflectionApi {
    private static class Animal {
        private int a;
    }


    private static final class Dog extends Animal implements Jumpable, Swimable {
        public int b;
        private int c;

        Dog(int b, int c) {
            this.b = b;
            this.c = c;
        }

        @MyAnnotation
        private void voice() {
            System.out.println("Hav-Hav!");
        }

        @MyAnnotation(data = 23)
        private void info() {
            System.out.println("Dog info");
        }
    }

    private static class Cat {
        private int time;
        String name;

        private class Catty {
        }

        Cat() {
        }

        private void jump(Integer time) {
        }

        @MyAnnotation
        private void meow(int time) {
        }

        private void doSomethings() {
        }
    }

    /* Точка входа */
    public static void main(String[] args) throws Exception {
        m1();  /* Получим сылку на объект класса */
        m2();  /* Как отображаются примитивные массивы */
        m3();  /* Что можно получить из класса рефлексией */
        m4();  /* Поля, модификаторы, конструкторы, методы, аннотации */
        m5();  /* Расшифровка модификаторов */
        m6();  /* Получить список интерфейсов класса */
        m7();  /* Показать поля класса */
        m8();  /* Что можем спросить у поля */
        m9();  /* Установка значений в поле */
        m10(); /* Конструкторы */
        m11(); /* Методы */
        m12(); /* Загрузка классов. Плагин */
    }

    /* Загрузка классов. Плагин */
    private static void m12() throws Exception {
        URL[] folderUrls = {new URL("file:\\src\\ru\\davidlevi\\lesson7\\classwork\\")};
        Class c = URLClassLoader.newInstance(folderUrls).loadClass("ru.davidlevi.lesson7.classwork.Unknown");
        Object o = c.getConstructor(int.class, int.class).newInstance(5, 5);
        Method m = c.getMethod("doSomething");
        m.invoke(o);
        // Unknown class method doSomething (5 5)
    }

    /* Методы */
    private static void m11() throws Exception {
        /* Получим все методы: */
        Class cs = Dog.class;
        Method[] methods = cs.getDeclaredMethods();
        for (Method o : methods)
            System.out.println(o);
        // Получим:
        // private java.lang.String ru.davidlevi.lesson7.classwork.ReflectionApi$Dog.voice()

        /* Вызов метода: */
        Class dogClass = Dog.class;
        Dog dog = new Dog(1, 2);
        Method[] methodsOfDog = dogClass.getDeclaredMethods();
        /* потому что метод приватный поэтому даем доступ */
        methodsOfDog[0].setAccessible(true);
        methodsOfDog[0].invoke(dog); // выполнится метод проиндексированный числом 0
        /* потому что метод приватный поэтому даем доступ */
        methodsOfDog[1].setAccessible(true);
        methodsOfDog[1].invoke(dog); // выполнится метод проиндексированный числом 1
        // Получим:
        // Dog info
        // Hav-Hav!

        Method method = dogClass.getDeclaredMethod("info");
        /* потому что метод приватный поэтому даем доступ */
        method.setAccessible(true);
        method.invoke(dog); // Dog info

    }

    /* Конструкторы */
    private static void m10() throws Exception {
        /* Получим конструктор, создадим и инициируем переменную: */
        Constructor<String> constructor = String.class.getConstructor(String.class);
        String a = constructor.newInstance("Java");
        // аналогично String a = new String("Java");

        /* Запросим у класса все его конструкторы: */
        Constructor[] constructors = String.class.getConstructors();
        for (Constructor o : constructors)
            System.out.println(o);

        /* Получим отдельный конструктор.
        Берем из public java.lang.String(byte[]) и получаем byte[].class, его и указываем в качестве параметра: */
        Constructor cl = String.class.getConstructor(byte[].class); // public java.lang.String(byte[])

        /* Обращение к конструктору: */
        Constructor<String> cn = String.class.getConstructor(String.class);
        /* аналогично String a = new String("Java"); */
        String de3 = cn.newInstance("Java");

        /* Создать объект по конструктору: */
        Constructor constructor1 = String.class.getConstructor(byte[].class);
        String s = (String) constructor1.newInstance((Object) new byte[]{65, 66, 67});
        System.out.println(s);  // ABC

        /* Создание объекта по индексированному конструктору: */
        Constructor constructor2 = String.class.getConstructors()[7]; // 7 = byte[].class
        String s1 = (String) constructor2.newInstance((Object) new byte[]{65, 66, 67});
        System.out.println(s1);

        /* Получение параметров конструктора: */
        Constructor constructor3 = String.class.getConstructors()[0]; // это public java.lang.String(byte[],int,int)
        System.out.println(constructor3.getParameterCount()); // 1
        Class[] params = constructor3.getParameterTypes();
        System.out.println(Arrays.toString(params)); // параметры данного конструктора [class [B, int, int]

        /* Другие параметры конструкторов: */
        constructor3.getParameterCount(); // количество параметров
        //constructor3.newInstance(); // новый инстанс (экземпляр)
        constructor3.getParameterTypes(); // типы параметров
        constructor3.isVarArgs(); // есть ли в конструкторе аргументы переменной длины?
    }

    /* Установка значений в поле: */
    private static void m9() throws Exception {
        Cat cat = new Cat();
        Field field = cat.getClass().getDeclaredField("name");
        field.set(cat, "Murzik");

        /* По ООП мы знаем, если поле приватное в классе, то мы не сможем изменить его из другого класса,
        а вот с помощью рефлексии можно установить значение: */
        Class cs = Dog.class;
        Field declaredField = cs.getDeclaredField("c");
        declaredField.setAccessible(true); // !
        Dog dog = new Dog(1, 2);
        declaredField.set(dog, 100);
        System.out.println(declaredField.get(dog)); // Установили значение 100
    }

    /* Что можем спросить у поля: */
    private static void m8() throws Exception {
        Class cs = Dog.class;

        Field[] fields = cs.getFields();
        //fields[0].getAnnotations(); // можем получить аннотацию
        //fields[0].getType();  // можем получить тип поля
        //fields[0].getName(); // его имя

        /* Здесь будем получать значение из приватной переменной класса с */
        Field field = cs.getDeclaredField("c");
        field.setAccessible(true);
        Dog dog = new Dog(1, 2);
        System.out.println(field.get(dog)); // 2

    }

    /* Показать поля класса: */
    private static void m7() {
        Class cs = Dog.class;

        /* Показать только публичные поля getFields() */
        Field[] fields1 = cs.getFields();
        for (Field o : fields1)
            System.out.println(o);
        // Только:
        // b. public int ru.davidlevi.lesson7.MainClass$Dog.b

        /* Показать задекларированные поля getDeclaredFields() */
        Field[] fields2 = cs.getDeclaredFields();
        for (Field o : fields2)
            System.out.println(o);
        // Только:
        // public int ru.davidlevi.lesson7.MainClass$Dog.b
        // private int ru.davidlevi.lesson7.MainClass$Dog.c

        /* Показать задекларированные поля суперкласса */
        Class superclass = cs.getSuperclass();
        Field[] fields3 = superclass.getDeclaredFields();
        for (Field o : fields3)
            System.out.println(o);
        // Только:
        // private int ru.davidlevi.lesson7.MainClass$Animal.a
    }

    /* Получить список интерфейсов класса Dog: */
    private static void m6() {
        Class[] interfaces = Dog.class.getInterfaces();
        for (Class oneOf : interfaces)
            System.out.println(oneOf.getSimpleName());
    }

    /* Расшифровка модификаторов: */
    private static void m5() {
        int modificators = String.class.getModifiers();
        System.out.println(Modifier.isFinal(modificators));  // true потому что строки у нас final, неизменяемые.
        System.out.println(Modifier.isInterface(modificators));  // false
        System.out.println(Modifier.isAbstract(modificators)); // false

        /* Аналогично с isPublic(), isPrivate(), isAbstract(), isFinal(), isNative(), isInterface(),
        isSynchronized(), isVolatile(), isStrict(), isTransient(), isProtected(), isStatic().
        */
    }

    /* Поля, модификаторы, конструкторы, методы, аннотации */
    private static void m4() throws Exception {
        Class aClass = String.class;

        /* получить поля класса */
        Field[] arrayFields = aClass.getFields();

        /* получить методы класса */
        Method[] arrayMethods = aClass.getMethods();

        /* получить задекларированные методы */
        Method[] methods = Cat.class.getDeclaredMethods();
        for (Method o : methods)
            System.out.println(o.getReturnType() + " ||| " + o.getName() + " ||| " + Arrays.toString(o.getParameterTypes()));

        /* получить методы из класса Cat по имени*/
        //Method m1 = Cat.class.getDeclaredMethod("jump", null);
        Method m2 = Cat.class.getDeclaredMethod("meow", int.class);

        /* получить метод класса по имени */
        Method m = Cat.class.getDeclaredMethod("doSomethings");

        /* получить аннотации */
        Method[] methodsAll = Dog.class.getDeclaredMethods();
        for (Method oneOf : methodsAll)
            System.out.println(oneOf.getName() + " with " + oneOf.getDeclaredAnnotation(MyAnnotation.class));
        // Получим:
        // info with @ru.davidlevi.lesson7.classwork.MyAnnotation(data=23, order=10)
        // voice with @ru.davidlevi.lesson7.classwork.MyAnnotation(data=5, order=10)
        for (Method oneOf : methods) {
            if (oneOf.isAnnotationPresent(MyAnnotation.class)) {
                System.out.println(oneOf.getAnnotation(MyAnnotation.class).order());
                System.out.println(oneOf.getAnnotation(MyAnnotation.class).data());
            }
        }
        // Получим:
        // 10
        // 5

        /* получает модификатор (private, public, protected, static, abstract, synchronized и т.п.) */
        Class dog = Dog.class;
        String name = dog.getSimpleName();
        int mods = dog.getModifiers();
        if (Modifier.isPublic(mods)) {
            System.out.println(name + " public");
        }
        if (Modifier.isAbstract(mods)) {
            System.out.println(name + " abstract");
        }
        if (Modifier.isFinal(mods)) {
            System.out.println(name + " final"); // Dog final
        }
        if (Modifier.isPrivate(mods)) {
            System.out.println(name + " privat"); // Dog privat
        }
        if (Modifier.isStatic(mods)) {
            System.out.println(name + " static"); // Dog static
        }

        /* получить конструктор(ы) */
        Constructor c = Cat.class.getDeclaredConstructor();
        Constructor[] arrayConstructors = Cat.class.getConstructors();
        // Получим: [public java.lang.String(byte[],int,int), public java.lang.String(byte[],java.nio.charset.Charset), public java.lang.String(byte[],java.lang.String) throws java.io.UnsupportedEncodingException...

        /* Получение задекларированных данных, в том чисте и приватные поля */
        Class cat = Cat.class;
        System.out.println(Arrays.toString(cat.getDeclaredClasses()));
        System.out.println(Arrays.toString(cat.getDeclaredFields()));
        System.out.println(Arrays.toString(cat.getDeclaredMethods()));
    }

    /* Что можно получить из класса рефлексией */
    private static void m3() {
        Class aClass = String.class;

        /* Получение публичных (public) данных */
        System.out.println(aClass.getName());       // java.lang.String
        System.out.println(aClass.getSimpleName()); // String (имя класса)
        System.out.println(aClass.getClass());      // class java.lang.Class
        System.out.println(aClass.getSuperclass()); // class java.lang.Object
        System.out.println(aClass.getGenericSuperclass()); // class java.lang.Object
        System.out.println(aClass.getPackage()); // package java.lang, Java Platform API Specification, version 1.8 - получим имя пакета класса String

        /* Для получения всей цепочки родительских классов, достаточно рекурсивно вызывать
        метод getSuperclass(), до получения null (Object.class.getSuperclass() вернет null,
        так как у него нет родительского класса). */
    }

    /* Как отображаются примитивные массивы */
    private static void m2() {
        System.out.println(int.class);    // int
        System.out.println(int[].class);  // class [I
        System.out.println(int[][].class);// class [[I
        System.out.println(byte[].class); // class [B
        System.out.println(char[].class); // class [C
        System.out.println(float[].class);// class [F
    }

    /* Получим сылку на объект класса */
    private static void m1() throws Exception {
        Class aClass;

        /* вариант 1. Получить ссылку на класс описывающий тип Integer */
        Integer a = 10;
        aClass = a.getClass();

        /* вариант 2. Получить ссылку на класс описывающий тип String */
        aClass = String.class;

        /* вариант 3. Получить ссылку на класс описывающий тип String */
        aClass = Class.forName("java.lang.String");
    }
}

interface Jumpable {
}

interface Swimable {
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface MyAnnotation {
    int order() default 10;
    int data() default 5;
}
