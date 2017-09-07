package ru.davidlevy.conspects.collections;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class MainClass {
    private static MyBox box1 = new MyBox(1, 1);
    private static MyBox box2 = new MyBox(2, 2);
    private static MyBox box3 = new MyBox(3, 3);
    private static MyBox box4 = new MyBox(1, 1);
    //
    private static List<String> list = new ArrayList<>();
    private static ArrayList<String> arrayList = new ArrayList<>();
    private static Set<MyBox> set = new HashSet<>();
    private static Map<String, Integer> map = new HashMap<>();

    public static void main(String[] args) {
        // Интерфейс Collection описывает любые коллекции в иерархии
        // (Set Map SortedSet SortedMap HashSet TreeSet ArrayList LinkedList Vector Collections Arrays AbstractCollection)
        gCollection();

        // Интерфейс List
        // - дубли разрешены
        // - List, ArrayList (индексы): быстрый доступ, порядок добавления сохранен
        // - LinkedList (сохранен порядок добавления): добавление и удаление быстро, чтение медленно
        gList();

        // Интерфейс Set (множество, набор)
        // - дубли объектов запрещены
        // - хэш вместо индексов: очень быстрый поиск
        // - HashSet (порядок добавления не сохранен)
        // - LinkedHashSet (сохранен порядок добавления)
        // - TreeSet (= HashSet c сортировкой) реализуется с интерефейсом Comparable
        gSet();

        // Интерфейс Map (словарь)
        // - дубли объектов запрещены
        // - хэш вместо индексов: очень быстрый поиск
        // - HashMap (порядок добавления не сохранен)
        // - LinkedHashMap (сохранен порядок добавления)
        // - *TreeMap (= HashMap c сортировкой) реализуется с интерефейсом Comparable
        gMap();

        // Перебор элементов коллекций
        gIteration();

        // equals (contains)
        gEquals();

        // Stream API, см. https://habrahabr.ru/company/luxoft/blog/270383/
        gStreamAPI();
    }

    private static void gCollection() {
        // Создание коллекций:
        // вариант 1
        Collection<String> stringCollection1 = Arrays.asList("A", "B", "C"); // Arrays.asList(new String[]{"A", "B", "C"});
        Collection<Boolean> booleanCollection = Arrays.asList(false, false, true);
        // вариант 2
        Collection<String> stringCollection2 = new ArrayList<>();
        Collection<Integer> integerCollection = new HashSet<>();
        // вариант 3
        List<String> stringList = new ArrayList<>();
        Set<Integer> integerSet = new HashSet<>();
        // вариант 4
        ArrayList<String> stringArrayList = new ArrayList<>();
        HashSet<Integer> integerHashSet = new HashSet<>();
    }

    private static void gList() {
        // List - интерфейс
        list.isEmpty(); // пустой ли список?
        list.clear();
        list.size();
        list.add("list");

        // ArrayList — основан на массиве
        arrayList.addAll(list);
        arrayList.add("array");
        arrayList.add(1, "line");
        arrayList.add("string"); // дубри разрешены
        //System.out.println(arrayList.get(0));  // array
        //System.out.println(arrayList.indexOf("line")); // 1
        arrayList.remove(1);
        arrayList.trimToSize();

        // LinkedList — на основе двухстороннего связанного списка
        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add("second");
        linkedList.addFirst("first");
        linkedList.addLast("last");
        linkedList.getFirst(); // Получает первый элемент списка, но если его нет, то возвращает искл. new NoSuchElementException()
        linkedList.peekFirst(); // Получает первый элемент списка, но если его нет, то возвращает null
        linkedList.pollFirst(); // Извлекает (получает и удаляет) первый элемент списка
    }

    private static void gSet() {
        // Set - интерфейс

        // HashSet - набор
        set.add(box1);
        set.add(box2);
        set.add(box3);
        set.add(box4); // не добавит потому, что хэш-коды равны
        set.add(box1); // дубли запрещены
        //[Box(3,3), Box(2,2), Box(1,1)] - порядок не гарантирован
        Set<MyBox> set1 = new HashSet<>(100); // вместимость
        Set<MyBox> set2 = new HashSet<>(200, 0.75f); // фактор увеличения вместимости

        // LinkedHashSet — сохранен порядок добавления
        LinkedHashSet<MyBox> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.addAll(set);
        //[Box(1,1), Box(2,2), Box(3,3)]

        // TreeSet - это HashSet + сортировка (MyBox implement Comparable)
        Set<MyBox> myBoxSet = new TreeSet<>();
        myBoxSet.add(box2);
        myBoxSet.add(box3);
        myBoxSet.add(box1);
        // [Box(1,1), Box(2,2), Box(3,3)]
        //
        // Сортировка через переопределение метода анонимного класса используется,
        // если интерфейс Comparable не имплементирован в класс MyBox.
        Set<MyBox> myBoxSet1 = new TreeSet<>(new Comparator<MyBox>() {
            @Override
            public int compare(MyBox o1, MyBox o2) {
                return o1.area() - o2.area();
            }
        });
        //
    }


    private static void gMap() {
        // Map - интерфейс

        // HashMap - словарь
        map.put("пятница", 5);
        map.put("понедельник", 1);
        map.put("вторник", 0);
        map.put("вторник", 2); // новый ключ "вторник" затирает старый и соответственно значение
        map.put("среда", 3);
        map.put("четверг", 4);
        //{вторник=2, среда=3, пятница=5, понедельник=1, четверг=4}
        map.containsKey("понедельник"); // true (есть ли ключ "понедельник"?)
        map.containsValue(6); // false (есть ли значение 6?)

        // TreeMap - это HashMap + сортировка (MyBox implement Comparable)
        Map<MyBox, Integer> myBoxIntegerMap = new TreeMap<>();
        /*
        Map<MyBox, Integer> myBoxIntegerMap = new TreeMap<>(new Comparator<MyBox>() {
            @Override
            public int compare(MyBox o1, MyBox o2) {
                return o1.area() - o2.area();
            }
        });
        */
        myBoxIntegerMap.put(new MyBox(3, 3), 3);
        myBoxIntegerMap.put(new MyBox(1, 1), 1);
        myBoxIntegerMap.put(new MyBox(2, 2), 2);
    }

    private static void gIteration() {
        // Перебор по индексам
        for (int i = 0; i < arrayList.size(); i++) {
            //System.out.println(arrayList.get(i));
        }

        // Перебор foreach
        for (String s : arrayList) {
            //System.out.println(s);
        }

        // Перебор иттератором
        Iterator<String> stringIterator = list.iterator();
        while (stringIterator.hasNext()) {
            String s = stringIterator.next();
            //System.out.println(s);
        }

        // Метод forEach
        //arrayList.forEach(System.out::println);

        // Перебор map через множество (set) записей Map.Entry<K,V>
        // пример 1
        Set<Map.Entry<String, Integer>> entrySet = map.entrySet();
        Iterator<Map.Entry<String, Integer>> entryIterator = entrySet.iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, Integer> entry = entryIterator.next();
            entry.getKey(); // получаем ключ
            entry.getValue(); // ... значение
        }
        // пример 2
        //Set<Map.Entry<String, Integer>> entrySet = map.entrySet();
        for (Map.Entry<String, Integer> entry : entrySet) {
            entry.getKey(); // получаем ключ
            entry.getValue(); // ... значение
        }
        // пример 3. Метод forEach и интерфейс BiConsumer
        map.forEach(new BiConsumer<String, Integer>() {
            @Override
            public void accept(String s, Integer integer) {
                System.out.println(s + "=" + integer);
            }
        });
    }

    private static void gEquals() {
        ArrayList<MyBox> boxes = new ArrayList<>();
        boxes.add(box1);
        boxes.add(box2);
        boxes.add(box3);
        //System.out.println(box1.equals(box1)); // true
        //System.out.println(box1.equals(box2)); // false
        //System.out.println(box1.equals(boxes)); // false
        //System.out.println(boxes.contains(box1)); // true
    }

    private static void gStreamAPI() {
        // Перебор через потоки
        //arrayList.stream().forEach(System.out::println);
        //arrayList.stream().forEach(a -> System.out.println(a));
        //Arrays.asList(new String[]{"1","1","1","1","1"}).forEach(e -> System.out.print(e + " ")); // 1 1 1 1 1

        // Создание потока из коллекции
        Stream<Integer> integerStream = Arrays.asList(1, 2, 3).stream();

        // Создание потока из значений
        Stream<Integer> integerStream1 = Stream.of(1, 2, 3);

        // Создание потока из массива
        Stream<Integer> integerStream2 = Arrays.stream(new Integer[]{1, 2, 3});
    }
}

class MyBox implements Comparable<MyBox> {
    private int width;
    private int height;

    MyBox(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int area() {
        return this.width * this.height;
    }

    @Override
    public String toString() {
        return "Box(" + this.width + "," + this.height + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // если это я, то true
        if (!(o instanceof MyBox)) return false; // если это не мой тип, то false
        MyBox box = (MyBox) o;
        // мы считаем нужным, что длина и ширина соответственно равны
        return (this.width == box.width) & (this.height == box.height);
    }

    @Override
    public int hashCode() {
        // 1. если по equals объекты равны, то и хеш-коды должны быть равны
        // 2. если хеш-коды разные, то объекты всегда разные
        // Коллизия возникает, когда хеш-коды равны, но по equals не равны.
        return (31 * this.width) + this.height;
    }

    // для реализации сравнения объектов, см. TreeSet
    @Override
    public int compareTo(MyBox o) {
        // мы считаем нужным, что будем сортировать по площади
        return (this.width * this.height) - (o.width * o.height);
    }
}