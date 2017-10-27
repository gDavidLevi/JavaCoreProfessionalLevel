package ru.davidlevi.lesson4.homework;

public class Task3 {
    private static class Mfu {
        /* Данные мониторы не дадут одновременно нескольким потокам печатать (сканировать) */
        private static final Object MONITOR_PRINTER = new Object();
        private static final Object MONITOR_SCANER = new Object();

        private int pauseMs;

        Mfu(int pauseMs) {
            this.pauseMs = pauseMs;
        }

        void scaner(int pages) {
            synchronized (MONITOR_SCANER) {
                for (int i = 0; i < pages; i++) {
                    System.out.printf(" отсканирована %d страница%n", (i + 1));
                    try {
                        Thread.sleep(this.pauseMs);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        void printer(int pages) {
            synchronized (MONITOR_PRINTER) {
                for (int i = 0; i < pages; i++) {
                    System.out.printf(" напечатана %d страница%n", (i + 1));
                    try {
                        Thread.sleep(this.pauseMs);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /* Точка входа */
    public static void main(String[] args) {
        /*
         * Написать класс МФУ, на котором возможны одновременная печать и сканирование документов, при этом нельзя
         * одновременно печатать два документа или сканировать (при печати в консоль выводится сообщения "отпечатано
         * 1, 2, 3,... страницы", при сканировании тоже самое только "отсканировано...", вывод в консоль все также
         * с периодом в 50 мс.)
         */
        System.out.println("МФУ");

        /* Основная логика */
        Mfu mfu = new Mfu(50);
        new Thread(() -> mfu.printer(3)).start();
        new Thread(() -> mfu.printer(3)).start();
        new Thread(() -> mfu.scaner(2)).start();
        new Thread(() -> mfu.scaner(2)).start();
    }
}