package ru.davidlevi.conspects.generic;

import java.util.ArrayList;

import static java.util.Arrays.asList;

interface Favorites {
}

interface Domestic {
}

/**
 * Обобщения (Generic) - это параметризованные типы. С их помощью можно объявлять классы, интерфейсы и методы, где тип
 * данных указан в виде параметра. Обобщения добавили в язык безопасность типов.
 * <p>
 * https://docs.oracle.com/javase/tutorial/java/generics/types.html
 * https://urvanov.ru/2016/04/28/java-8-обобщения/
 * https://habrahabr.ru/post/329550/
 * https://habrahabr.ru/post/334512/
 */
public class MainClass {
    /* Точка входа */
    public static void main(String[] args) {
        step1_Object();
        step2_Generic();
        step3_Limitations();
        step4_Methods();
        step5_GenericInterfaces();
    }

    /**
     * Метод compare является расширением обобщенного интерфейса Comparable<T>.
     * Метод сравнивает только одинаковые типы, см. Comparable<T>.     *
     *
     * @param t1  это
     * @param t2  сравнисть с этим
     * @param <T> Generic
     * @return int
     */
    public static <T extends Comparable<T>> int compare(T t1, T t2) {
        return t1.compareTo(t2);
    }

    private static void step5_GenericInterfaces() {
        System.out.println("step5_GenericInterfaces() ::");
    }

    /**
     * Копирование из одного arrayList в другой
     * В параметрах мы указали, что можем копировать наследников в родителей или родителя в родителя.
     *
     * @param source      расширяет тип <T>
     * @param destination является наследником <T>
     * @param <T>         Generic
     * @return boolean
     */
    private static <T> void copyElements(ArrayList<? extends T> source, ArrayList<? super T> destination) {
        for (int i = 0, quantity = source.size(); i < quantity; i++)
            destination.add(source.get(i));
        source.clear();
    }

    /* Сравнение списков. Тип един для двух параметров */
    private static <T extends Number> boolean compareTwoArrayList(ArrayList<T> arrayList1, ArrayList<T> arrayList2) {
        return arrayList1.containsAll(arrayList2);
    }

    /* Сравнение списков. Разные типы в параметрах */
    private static <T1 extends Integer, T2 extends Number> boolean compareTwoArrayListVer2(ArrayList<T1> arrayList1, ArrayList<T2> arrayList2) {
        return arrayList1.containsAll(arrayList2);
    }

    /**
     * Возвращает среднее значение.
     *
     * @param array принимает любой тип до класса Number включительно
     * @return double
     */
    private static double calcAvg(ArrayList<? extends Number> array) {
        double result = 0d;
        final int quantity = array.size();
        for (int i = 0; i < quantity; i++) result += array.get(i).doubleValue();
        result /= quantity;
        return result;
    }

    /**
     * Аннотация @SafeVarargs относится к документируемой части объявления метода.
     * Эта аннотация говорит, что эта реализация метода корректно обрабатывает varargs-параметр.
     *
     * @param elements T...
     * @param <T>      Generic
     */
    @SafeVarargs
    private static <T> String safeVarArgs(T... elements) {
        StringBuilder stringBuilder = new StringBuilder();
        for (T oneFrom : elements)
            stringBuilder.append(oneFrom);
        return stringBuilder.toString();
    }

    private static void step4_Methods() {
        System.out.print("step4_Methods() ::");

        ArrayList<Integer> integerArrayList = new ArrayList<>();
        integerArrayList.add(2);
        integerArrayList.add(4);
        System.out.println(calcAvg(integerArrayList)); // 3.0

        ArrayList<Float> floatArrayList1 = new ArrayList<>();
        floatArrayList1.add(1.1f);
        floatArrayList1.add(2.2f);
        ArrayList<Float> floatArrayList2 = new ArrayList<>();
        floatArrayList2.add(1.1f);
        floatArrayList2.add(2.2f);
        System.out.println(compareTwoArrayList(floatArrayList1, floatArrayList2)); // true

        ArrayList<Integer> integerArrayList1 = new ArrayList<>();
        integerArrayList.add(1);
        integerArrayList.add(2);
        System.out.println(compareTwoArrayListVer2(integerArrayList1, floatArrayList2)); // false

        ArrayList<Integer> fromList = new ArrayList<>();
        fromList.add(10);
        fromList.add(20);
        ArrayList<Number> toList = new ArrayList<>();
        copyElements(fromList, toList);
        System.out.println(toList); // [10, 20]

        System.out.println(safeVarArgs("2", "+", "4", "=", "6")); // 2+4=6
    }

    private static void step3_Limitations() {
        System.out.println("step3_Limitations() ::");
        /**
         * extends - Тип и его наследники
         * Класс BoxLimitations параметризуется типом Number и его наследниками: Float, BigDecimal, Long, Double, Integer, Byte...
         */
        class BoxLimitations<T extends Number> {
            private T[] array;

            private BoxLimitations(T[] array) {
                this.array = array;
            }

            private double avg() {
                double result = 0d;
                final int quantity = this.array.length;
                for (int i = 0; i < quantity; i++)
                    result += array[i].doubleValue();
                result /= quantity;
                return result;
            }

            @Override
            public boolean equals(Object obj) { // через класс Object с приведением к типу BoxLimitations
                return Math.abs(avg() - ((BoxLimitations) obj).avg()) < 0.0001f;
            }

            private boolean equalsA(BoxLimitations<?> obj) {  // <?> передаёт любой тип в качестве параметра
                return Math.abs(avg() - obj.avg()) < 0.0001f;
            }

            /* альтернатива equalsA() */
            private boolean equalsB(BoxLimitations obj) { // тип без параметров типа
                return Math.abs(avg() - obj.avg()) < 0.0001f;
            }
        }

        /* String не является наследником класса Number, поэтому Java на него ругается */
        //BoxLimitations<String> stringBoxLimitations = new BoxLimitations<>(new String[]{"1", "2", "3", "4"});
        BoxLimitations<Integer> integerBoxLimitations = new BoxLimitations<>(new Integer[]{1, 2, 3, 4});
        BoxLimitations<Long> longBoxLimitations = new BoxLimitations<>(new Long[]{1L, 2L, 3L, 4L});
        BoxLimitations<Float> floatBoxLimitations = new BoxLimitations<>(new Float[]{1f, 2f, 3f, 4f});

        /* Вычисление среднего значения */
        integerBoxLimitations.avg(); // 2.5
        longBoxLimitations.avg(); // 2.5
        floatBoxLimitations.avg(); // 2.5

        /* Сравнение коробок с разными параметризированными типами  */
        integerBoxLimitations.equals(longBoxLimitations); // true
        integerBoxLimitations.equalsA(longBoxLimitations); // true
        integerBoxLimitations.equalsB(longBoxLimitations); // true

        class Animal {
        }
        class Chordata extends Animal {
        }
        class Reptilia extends Chordata {
        }
        class Mammalia extends Chordata {
        }
        class Ornithurae extends Chordata {
        }
        class Squamata extends Reptilia {
        }
        class Perissodactyla extends Mammalia {
        }
        class Equus extends Perissodactyla implements Favorites {
        }
        class Carnivora extends Mammalia {
        }
        class Felis extends Carnivora implements Favorites {
        }
        class Canis extends Carnivora implements Favorites {
        }

        /*
         * Осевой скелет есть у всех хордовых животных поэтому параметризованный тип может принимать типы:
         * Reptilia (пресмыкающиеся), Mammalia (млекопитающие), Ornithurae (веерохвостые) и
         * их наследников: Squamata, Perissodactyla, Equus, Carnivora, Felis, Canis
         */
        class AxialSkeleton<T extends Chordata> {
        }

        /* Тип Тигр может создать объект с параметризированным классом животных кошки Felis
         *  и их наследниками: Carnivora, Felis, Tiger
         */
        class Tiger<T extends Carnivora> extends Felis {
        }

        /* Тип Лошадь может создать объект с параметризированным классом животных непарнокопытные (Reptilia)
         * и их наследниками: Perissodactyla, Equus, Horse
         */
        class Horse<T extends Perissodactyla> extends Equus implements Domestic {
        }

        /*
         * Класс может параметризоваться типами от млекопитающихся (Mammalia) реализующих интерфейс Favorites, то есть
         * это только: лошади (Horse), кошки (Felis), волки (Canis)
         */
        class MyFavorites<T extends Mammalia & Favorites> {
        }

        /*
         * Класс может параметризоваться типами от интерфейса Domestic, то есть это только лошадь (Horse)
         */
        class MyDomestic<T extends Domestic> {
        }

        /*
         * super - Тип и его родители
         * Класс MyDomestic параметризуется типом Squamata и его родителями: Squamata, Reptilia, Chordata, Animal
         */
        //class MyDomestic<? super Squamata> { }
    }

    private static void step2_Generic() {
        System.out.println("step2_Generic() ::");

        /*
         * Класс Коробка созданный с параметризованным типом
         *
         * @param <T> любой неприметивный тип
         */
        class BoxGeneric<T> {
            private T obj;

            private BoxGeneric(T obj) {
                this.obj = obj;
            }

            private void setObj(T obj) {
                this.obj = obj;
            }

            private void info() {
                System.out.println(" Class: " + obj.getClass().getName() + " | Object: " + obj);
            }

            private T getObj() {
                return obj;
            }
        }

        /* Явное указание типа String в качесвте параметра */
        BoxGeneric<String> boxGeneric = new BoxGeneric<>("Java");
        boxGeneric.setObj("100");

        /* Извлекаем объект без приведения к типу */
        String result = boxGeneric.getObj();

        /*
         * Универсальные маркеры обозначения типов:
         * T — любой тип
         * E — элемент коллекции (еlement, обширно используется Java Collections Framework)
         * K — ключ (key)
         * V — значение (value)
         * N — число (numerique)
         * S, U, V и т. п. — 2-й, 3-й, 4-й типы
         * можно использовать свои маркеры, например: TYPE5, BINTYPE, ELEMENT...
         */

        class Simple<S, N, A, I> {
            private S string;
            private N number;
            private A array;

            private Simple(S string, N number, A array) {
                this.string = string;
                this.number = number;
                this.array = array;
            }

            private void info() {
                System.out.println(this.string + " | " + this.number + " | " + this.array);
            }
        }

        /* До тех пор пока не будет скомпилирован обобщенный класс Java не сможет создать объект обобщенного типа,
        * поэтому запись вида T myObj = new T(); будет помечена как ошибочная ещё до компиляции. */

        /* Инициализация полей обобщенного класса должна происходить снаружи обобщенного класса */
        String string = "Pascal";
        Integer integer = 2;
        ArrayList<Integer> integerArrayList = new ArrayList<>(asList(1, 2, 3, 4));
        Simple<String, Integer, ArrayList<Integer>, Integer[]> simple;
        simple = new Simple<>(string, integer, integerArrayList);
        simple.info();

        /* Если создать объект из обобщенного класса без указания типов параметров, то все значении внутри класса будут
         * на основе типа Object. Тем не менее так мы можем делать.
         * См. класс BoxObject.
         */
        Simple simple1 = new Simple<>(string, integer, integerArrayList);

        System.out.println();
    }

    private static void step1_Object() {
        System.out.println("step1_Object() ::");

        /* Класс Коробка c объектом класса Object */
        class BoxObject {
            private Object obj;

            private BoxObject(Object obj) {
                this.obj = obj;
            }

            private void setObj(Object obj) {
                this.obj = obj;
            }

            private void info() {
                System.out.println(" Class: " + obj.getClass().getName() + " | Object: " + obj);
            }

            private Object getObj() {
                return obj;
            }
        }

        BoxObject boxObject1 = new BoxObject("Java");
        BoxObject boxObject2 = new BoxObject("Pascal");

        boxObject1.info(); //  Class: java.lang.String | Object: Java
        boxObject2.info(); //  Class: java.lang.String | Object: Pascal
        /* Во вторую коробку поместим копию ссылки на объекта "Java" в первую коробку. */
        boxObject2.setObj(boxObject1.getObj());
        boxObject1.info(); // Class: java.lang.String | Object: Java
        boxObject2.info(); // Class: java.lang.String | Object: Java

        /* Извлекаем объект класса Object и приводим его к типу String */
        String temp = (String) boxObject1.getObj();

        /* Одно из решений: */
        String result = "";
        if (boxObject1.getObj() instanceof String)
            result = (String) boxObject1.getObj();
        System.out.println("Result: " + result);

        /*
         * Недостаток подобного подхода в том, что когда мы пользуемся Object'ами и у нас много кода, мы
         * можем запутаться к какому типу нужно кастовать полученный объект, или нам придется проверять на
         * тип используя instanceof.
         *
         * Для решения подобной проблемы придумали обобщения (Generic), в которых будет явно видно какой тип
         * возвращается. Хотя изночально они создавались для коллекций.
         */
        System.out.println();
    }
}