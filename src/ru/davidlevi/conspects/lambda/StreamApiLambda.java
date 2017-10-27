package ru.davidlevi.conspects.lambda;

import java.util.ArrayList;
import java.util.List;

public class StreamApiLambda {
    public static void main(String[] args) {
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Саша", 36));
        persons.add(new Person("Маша", 15));
        persons.add(new Person("Даша", 14));
        persons.add(new Person("Паша", 13));
        persons.add(new Person("Лёша", 32));
        persons.add(new Person("Гоша", 31));

        System.out.println("1. вывести коллекцию");
        for (Person p : persons) p.show();

        System.out.println("1.1 альтернативный вывод коллекции");
        persons.stream().forEach((Person p) -> p.show());

        System.out.println("1.2 альтернативный вывод коллекции (сокращенная запись, без stream())");
        persons.forEach(p -> p.show());

        System.out.println("1.3 сокращенный вывод");
        persons.forEach(Person::show);

        System.out.println("2. фильтрация");
        persons.stream().filter(p -> {
            return p.getAge() >= 18;
        }).forEach(Person::show);

        System.out.println("2.1 фильтрация (сокращенная запись)");
        persons.stream().filter(p -> p.getAge() >= 18).forEach(Person::toString);

        System.out.println("3. сортировка через компаратор");
        persons.stream().
                filter(p -> p.getAge() >= 18).
                sorted((p1, p2) -> p1.getName().compareTo(p2.getName())).
                forEach(Person::toString);

        System.out.println("3.1 показать только имена");
        persons.stream().
                filter(p -> p.getAge() >= 18).
                sorted((p1, p2) -> p1.getName().compareTo(p2.getName())).
                map(p -> p.getName()).
                forEach(System.out::println);

        System.out.println("4. среднее арифметическое");
        double averageAge = persons.stream().
                filter(p -> p.getAge() >= 18).
                mapToInt(p -> p.getAge()).
                average().getAsDouble();
        System.out.println(averageAge);
    }
}

class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void show() {
        System.out.printf("%s - %d\n", this.name, this.age);
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        System.out.printf("%s - %d\n", this.name, this.age);
        return super.toString();
    }
}