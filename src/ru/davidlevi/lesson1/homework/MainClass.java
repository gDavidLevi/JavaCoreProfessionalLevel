package ru.davidlevi.lesson1.homework;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Arrays.asList;

/**
 * Обобщения
 */
public class MainClass {
    /* Точка входа */
    public static void main(String[] args) {
       /* Поменяем два элемента местами в массиве */
        Integer[] array = new Integer[]{0, 1};
        System.out.println("до " + Arrays.toString(array));
        swapItemsOfArray(array, 0, 1);
        System.out.println("после" + Arrays.toString(array));

       /* Преобразуем массив в ArrayList */
        Integer[] arraySrc = new Integer[]{0, 1, 2, 3, 4, 5};
        ArrayList<Integer> result = convertArrayToArrayList(arraySrc);
        System.out.println("после" + result.toString());

       /* Большая задача: */
       /* - инициируем списки */
        ArrayList<Apple> appels1 = new ArrayList<>(asList(new Apple(), new Apple(), new Apple()));
        ArrayList<Orange> oranges = new ArrayList<>(asList(new Orange(), new Orange(), new Orange()));
        ArrayList<Apple> appels2 = new ArrayList<>(asList(new Apple(), new Apple(), new Apple()));

       /* - заполним коробки фруктами */
        Box<Apple> appleBox1 = new Box<>(appels1);
        Box<Orange> orangeBox = new Box<>(oranges);
        orangeBox.addFruit(new Orange()); // добавим ещё один апельсин
        Box<Apple> appleBox2 = new Box<>(appels2);

       /* - сравним */
        System.out.println("сравним коробки с фруктами (яблоки и апельсины) : " + appleBox1.compare(orangeBox));
        System.out.println("сравним коробки с фруктами (яблики и яблоки) : " + appleBox1.compare(appleBox2));

       /* - пересыпим фрукты */
        // appleBox1.moveAllTo(orangeBox); // нельзя пересыпать яблоки в коробку с апельсинами!
        System.out.println("до box1.count = " + appleBox1.getAmountOfFruit());
        System.out.println("до box3.count = " + appleBox2.getAmountOfFruit());
        appleBox1.moveAllTo(appleBox2);
        System.out.println("после box1.count = " + appleBox1.getAmountOfFruit());
        System.out.println("после box3.count = " + appleBox2.getAmountOfFruit());
    }

    /**
     * Поменять местами элементы в массиве.
     *
     * @param array      T[] массив
     * @param itemFirst  int первый индекс элемента
     * @param itemSecond int второй индекс элемента
     * @param <T>        любой ссылочный тип
     */
    private static <T> void swapItemsOfArray(T[] array, int itemFirst, int itemSecond) {
        T first = array[itemFirst];
        array[itemFirst] = array[itemSecond];
        array[itemSecond] = first;
    }

    /**
     * Метод преобразует массив в ArrayList.
     *
     * @param array T[] массив
     * @param <T>   любой ссылочный тип
     * @return <T>
     */
    private static <T> ArrayList<T> convertArrayToArrayList(T[] array) {
        ArrayList<T> resultList = new ArrayList<>();
        for (int i = 0, quantity = array.length; i < quantity; i++)
            resultList.add(array[i]);
        return resultList;
    }
}

/**
 * Класс Плод
 */
abstract class Fruit {
    float weight;

    float getWeight() {
        return weight;
    }
}

/**
 * Класс Яблоко
 */
class Apple extends Fruit {
    // Блок динамической инициализации. Только для объекта класса.
    {
        super.weight = 1.0f;
    }
}

/**
 * Класс Апельсин
 */
class Orange extends Fruit {
    {
        super.weight = 1.5f;
    }
}

/**
 * Коробка фруктов
 *
 * @param <T> все наследники класса Fruit
 */
class Box<T extends Fruit> {
    private ArrayList<T> content = new ArrayList<>();

    Box(ArrayList<T> fruits) {
        this.content = fruits;
    }

    void addFruit(T fruit) {
        this.content.add(fruit);
    }

    int getAmountOfFruit() {
        return this.content.size();
    }

    private float getWeight() {
        if (this.content.size() > 0)
            return (this.content.get(0).getWeight() * getAmountOfFruit());
        return 0;
    }

    boolean compare(Box<?> another) {
        return Math.abs(this.getWeight() - another.getWeight()) < 0.00001f;
    }

    void moveAllTo(Box<T> another) {
        for (T item : this.content)
            another.addFruit(item);
        this.content.clear();
    }
}