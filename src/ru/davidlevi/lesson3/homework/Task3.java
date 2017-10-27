package ru.davidlevi.lesson3.homework;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class Task3 {
    private static RandomAccessFile randomAccessFile = null;
    private static int PAGE_SIZE = 1800;

    /* Точка входа */
    public static void main(String[] args) throws IOException {
        System.out.println("Консольное приложение, которое умеет постранично читать текстовые файлы (размером > 10 Мб)");

        /* Инициализация */
        String WORK_DIR = "src/ru/davidlevi/lesson3/homework/";
        File file = null;
        int currentPage = 1;
        StringBuilder help = new StringBuilder();
        help.append("======================================================================================\n")
                .append("  Данная программа умеет постранично читать текстовые файлы размером > 10 Мб в UTF-8\n")
                .append("--------------------------------------------------------------------------------------\n")
                .append(" Комманды:\n")
                .append("  - открыть файл: open <file_name>; например: open story.txt\n")
                .append("  - отобразить страницу: page <number>\n")
                .append("  - закрыть файл и выйти: quit\n")
                .append("======================================================================================\n");

        /* Основная логика */
        System.out.println(help);
        Scanner scanner = new Scanner(System.in, "UTF-8");
        String userInput;
        String userCommand;
        boolean whileIsTerminated = false;
        while (!whileIsTerminated) {
            userInput = scanner.nextLine().toLowerCase().trim();
            userCommand = userInput.split(" ")[0];
            switch (userCommand) {
                case "open":
                    String fileName = WORK_DIR + userInput.split(" ")[1];
                    file = new File(fileName);
                    if (file.exists()) {
                        randomAccessFile = new RandomAccessFile(fileName, "r");
                        showPage(currentPage);
                    } else System.out.println("Файл не найден.");
                    break;
                case "page":
                    currentPage = Integer.parseInt(userInput.split(" ")[1]);
                    showPage(currentPage);
                    break;
                case "quit":
                    if (randomAccessFile != null) randomAccessFile.close();
                    whileIsTerminated = true;
                    break;
                default:
                    System.out.printf("Данная комманда \'%s\' не определена в программе.%n", userCommand);
                    System.out.println("Введите help для получения списка команд.");
                    break;
            }
        }
    }

    /**
     * Метод выводит страницу на экран.
     *
     * @param pageNumber номер страницы
     * @throws IOException ошибка
     */
    private static void showPage(int pageNumber) throws IOException {
        /* Начальная позиция чтения из файла */
        if (pageNumber == 1) randomAccessFile.seek(pageNumber - 1);
        else randomAccessFile.seek((pageNumber - 1) * PAGE_SIZE);

        /* Инициализация буфера */
        byte[] buffer = new byte[PAGE_SIZE];

        /* Чтение в byte[]-буфер */
        randomAccessFile.read(buffer);

        /* Преобразуем byte[]-буфер в строку символов UFT-8 для вывода на экран */
        System.out.println(new String(buffer, "UTF-8"));
        System.out.println("\n_____________________________________________________________ Page # " + pageNumber);
    }
}