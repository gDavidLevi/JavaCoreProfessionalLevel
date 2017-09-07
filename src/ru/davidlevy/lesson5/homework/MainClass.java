package ru.davidlevy.lesson5.homework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainClass {
    /* Количество участников */
    private static final int CARS_COUNT = 4;

    /**
     * Car - запускаемая часть для потока
     */
    private static class Car implements Runnable {
        private static int carsId;
        private static AtomicBoolean isWon;

        static {
            /* Счетчик участников */
            carsId = 0;
            /* Участник пока не победил, поэтому false по умолчанию для всех объектов */
            isWon = new AtomicBoolean(false);
        }

        private Race race;
        private int speed;
        private String name;
        private CyclicBarrier toBarrier;
        private CountDownLatch finishedCounter;

        /**
         * @param race            гоночная полоса
         * @param speed           скорость
         * @param finishedCounter счетчик финиширования
         * @param toBarrier       барьер готовности к старту
         */
        Car(Race race, int speed, CyclicBarrier toBarrier, CountDownLatch finishedCounter) {
            this.race = race;
            this.speed = speed;
            this.name = "Участник #" + (++carsId);
            this.toBarrier = toBarrier;
            this.finishedCounter = finishedCounter;
        }

        private String getName() {
            return name;
        }

        private int getSpeed() {
            return speed;
        }

        @Override
        public void run() {
            try {
                /* Подготовка */
                System.out.println(this.name + " готовится");
                Thread.sleep(500 + (int) (Math.random() * 800));
                System.out.println(this.name + " готов");

                /* К барьеру и ждать старта */
                this.toBarrier.await();
                //Thread.sleep(5); // hack позволяет выровнить вывод сообщений в консоль, но он не нужен в данной задаче потому, что используем конструктор new CyclicBarrier(n, new Runnable()...

                /* Старт! */
                for (int i = 0, quantity = this.race.getStages().size(); i < quantity; i++)
                    this.race.getStages().get(i).go(this);

                /* Финишировал */
                this.finishedCounter.countDown();

                /* Пришел первым? */
                if (!isWon.getAndSet(true))
                    System.out.println(this.name + " - WIN");
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Класс описывает гоночную полосу
     */
    private static class Race {
        private final ArrayList<Stage> stages;

        /* Инициируется массивом препятствий */
        Race(Stage... stages) {
            this.stages = new ArrayList<>(Arrays.asList(stages));
        }

        private ArrayList<Stage> getStages() {
            return stages;
        }
    }

    /**
     * Препятствие Дорога
     */
    private static class Road extends Stage {
        Road(int length) {
            super.length = length;
            super.description = "Дорога " + length + " метров";
        }

        @Override
        public void go(Car car) {
            try {
                System.out.println(car.getName() + " начал этап: " + getDescription());
                Thread.sleep(super.length / car.getSpeed() * 1000);
                System.out.println(car.getName() + " закончил этап: " + getDescription());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Препятствие Туннель
     */
    private static class Tunnel extends Stage {
        /* Пропускная способность тоннеля */
        private Semaphore tunnelLimit = new Semaphore(CARS_COUNT / 2);

        Tunnel() {
            super.length = 80;
            super.description = "Тоннель " + super.length + " метров";
        }

        @Override
        public void go(Car car) {
            try {
                try {
                    System.out.println(car.getName() + " готовится к этапу (ждет): " + getDescription());
                    tunnelLimit.acquire();
                    System.out.println(car.getName() + " начал этап: " + getDescription());
                    Thread.sleep(super.length / car.getSpeed() * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(car.getName() + " закончил этап: " + getDescription());
                    tunnelLimit.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* Некое препятствия */
    private static abstract class Stage {
        int length;
        String description;

        public abstract void go(Car c);

        String getDescription() {
            return description;
        }
    }

    /* Точка входа */
    public static void main(String[] args) throws BrokenBarrierException, InterruptedException, TimeoutException {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");

        /* Инициируем гоночную полосу */
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));

        /* Циклический барьер реализует подготовку к старту */
        CyclicBarrier toBarrier = new CyclicBarrier(CARS_COUNT + 1, new Runnable() {
            @Override
            public void run() {
                System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
            }
        });

        /* Счетчик отсчитывает пришедшего к финищшу участника */
        CountDownLatch finishedCounter = new CountDownLatch(CARS_COUNT);

        /* Инициируем участников */
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < CARS_COUNT; i++)
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10), toBarrier, finishedCounter);

        /* Запускаем поток машин */
        for (int i = 0; i < CARS_COUNT; i++)
            new Thread(cars[i]).start();

        /* Ожидаем, когда участники подготовятся к старту */
        toBarrier.await();

        /* Ожидаем, когда частники финишируют */
        finishedCounter.await();

        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка завершилась!!!");
    }
}