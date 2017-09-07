package ru.davidlevy.conspects.lambda;

import java.util.ArrayList;
import java.util.List;

public class EventSubscribe {
    private static void fire(Object sender) {
        System.out.println("privat static void fire (Object sender)");
    }

    /* Точка входа */
    public static void main(String[] args) {
        /* переключатель */
        Switcher switcher = new Switcher();

        /* подписка на событие (event subscribe) */
        switcher.addElectricityListener(new Lamp());
        switcher.addElectricityListener(new Radio());
        switcher.addElectricityListener(new Fire());
        switcher.addElectricityListener( // анонимный класс
                new ElectricityConsumer() {
                    @Override
                    public void electricityOn(Object sender) {
                        System.out.println(" Что-то ещё включили");
                    }
                }
        );

/*        // функциональный интерфейс = лямбда-выражение
        switcher.addElectricityListener((sender) -> System.out.println("вывод1"));
        switcher.addElectricityListener(sender -> {
            System.out.println("вывод2");
            System.out.println("вывод3");
        });

        // прямой вызов метода fire у класса MainClass6
        switcher.addElectricityListener(s -> MainClass6.fire(s));
        switcher.addElectricityListener(MainClass6::fire);  // альтернативная запись
*/

        /* включаем */
        switcher.switchOn();
    }
}

/*
 * Single Abstract Method lambda (SAM lambda).
 Одним из самых популярных способов использования этих интерфейсов заключается в создании анонимных внутренних классов.
 В Java 8 концепция SAM воссоздана и называется функциональными интерфейсами. Они могут быть представлены при помощи лямбда-выражений, ссылками на методы и конструкторами ссылок. Создана новая аннотация @FunctionalInterface которая используется для выдачи ошибок на уровне компилятора, когда интерфейс который Вы аннотировали не работает на функциональном уровне.
 */
@FunctionalInterface
interface ElectricityConsumer {
    /* у каждого устройства реализующего данный метод будет своя реализация */
    void electricityOn(Object object);
}

class Switcher {
    private List<ElectricityConsumer> listeners = new ArrayList<>();

    /* привязаться к переключателю */
    void addElectricityListener(ElectricityConsumer listener) {
        this.listeners.add(listener);
    }

    /* отвязаться от переключателя */
    public void removeElectricityListener(ElectricityConsumer listener) {
        this.listeners.remove(listener);
    }

    // переключатель включает все устройства реализующие интерфейс ElectricityConsumer
    void switchOn() {
        System.out.println("Выключатель включен:");
        for (ElectricityConsumer c : this.listeners) {
            c.electricityOn(this);
        }
    }
}

class Radio implements ElectricityConsumer {
    private void playMusic() {
        System.out.println(" Радио включилось");
    }

    @Override
    public void electricityOn(Object sender) {
        playMusic();
    }
}

class Lamp implements ElectricityConsumer {
    private void lightOn() {
        System.out.println(" Лампа зажглась");
    }

    @Override
    public void electricityOn(Object sender) {
        lightOn();
    }
}

class Fire implements ElectricityConsumer {
    @Override
    public void electricityOn(Object sender) {
        System.out.println(" Огонь горит");
    }
}