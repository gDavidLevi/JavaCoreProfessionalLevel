package ru.davidlevi.lesson4.homework;

public class Task1 {
    /* Последовательность символов */
    private static String sequence;

    /**
     * Класс MyRunnable - выполняемая часть потока
     */
    private static class MyRunnable implements Runnable {
        private static final Object MONITOR = new Object();
        private static char nextChar = sequence.charAt(0);

        private char firstChar;
        private char secondChar;
        private int time;

        public MyRunnable(int index, int time) {
            this.time = time;
            this.firstChar = sequence.charAt(index);
            this.secondChar = (index + 1 < sequence.length()) ? sequence.charAt(index + 1) : sequence.charAt(0);
        }

        @Override
        public void run() {
            synchronized (MONITOR) {
                for (int i = 0; i < this.time; i++) {
                    while (nextChar != this.firstChar)
                        try {
                            MONITOR.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    System.out.print(nextChar);
                    MONITOR.notifyAll();
                    nextChar = this.secondChar;
                }
            }
        }
    }

    /* Точка входа */
    public static void main(String[] args) {
        System.out.println("Создать три потока, каждый из которых выводит определенную букву (A, B и C) 5 раз, порядок должен быть именно ABCABCABCABCABC");
        System.out.print("Результат: ");

        /* Последовательность символов */
        sequence = "ABC";

        /* Количество повторений, раз */
        int time = 5;

        /*
        * Количество потоков вычисляется исходя из кол-ва символов в sequence. То есть 3.
        * Каждый поток работает с одним символом из sequence, по индексу начиная с 0.
        */
        for (int i = 0, quantity = sequence.length(); i < quantity; i++)
            new Thread(new MyRunnable(i, time)).start();
    }
}