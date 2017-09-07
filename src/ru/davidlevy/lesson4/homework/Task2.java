package ru.davidlevy.lesson4.homework;

import java.io.*;

public class Task2 {
    /**
     * Класс MyRunnable - выполняемая часть потока
     */
    private static class MyRunnable implements Runnable {
        private PrintStream printStream;
        private String record;
        private int quantityRecords;

        MyRunnable(PrintStream printStream, String record, int quantityRecords) {
            this.printStream = printStream;
            this.record = record;
            this.quantityRecords = quantityRecords;
        }

        @Override
        public void run() {
            for (int i = 0; i < this.quantityRecords; i++) {
                try {
                    /* Период записи 20 мс */
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.printStream.printf("%s | rec # %d%n", this.record, i);
            }
        }
    }

    /* Точка входа */
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Написать совсем небольшой метод, в котором 3 потока построчно пишут данные в файл (штук по 10 записей, с периодом в 20 мс)");
        method("src/ru/davidlevy/lesson4/homework/Task2.temp", 10);
    }

    /**
     * Метод, в котором три потока построчно пишут данные в файл
     *
     * @param filename        имя выходного файла
     * @param quantityRecords количество записей для записи
     * @throws InterruptedException ошибка
     * @throws IOException          ошибка
     */
    private static void method(String filename, int quantityRecords) throws InterruptedException, IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filename);
             PrintStream printStream = new PrintStream(fileOutputStream, true, "UTF-8");) {
            Thread thread1 = new Thread(new MyRunnable(printStream, "строка A", quantityRecords));
            Thread thread2 = new Thread(new MyRunnable(printStream, "строка B", quantityRecords));
            Thread thread3 = new Thread(new MyRunnable(printStream, "строка C", quantityRecords));
            thread1.start();
            thread2.start();
            thread3.start();
            thread1.join();
            thread2.join();
            thread3.join();
        }
    }
}