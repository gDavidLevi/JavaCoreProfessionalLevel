package ru.davidlevy.lesson3.homework;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class Task3Plus {
    private static RandomAccessFile randomAccessFile = null;
    private static final int PAGE_SIZE = 1800;

    /* Точка входа */
    public static void main(String[] args) throws IOException {
        System.out.println("Консольное приложение, которое умеет постранично читать текстовые файлы (размером > 10 Мб)");

        /* Инициализация */
        String WORK_DIR = "src/ru/davidlevy/lesson3/homework/";
        File file = null;
        int currentPage = 1;
        int quantityPages = 0;
        StringBuilder help = new StringBuilder();
        help.append("======================================================================================\n")
                .append("  Данная программа умеет постранично читать текстовые файлы размером > 10 Мб в UTF-8\n")
                .append("--------------------------------------------------------------------------------------\n")
                .append(" Комманды:\n")
                .append("  - показать эту справку: help\n")
                .append("  - открыть файл: open <file_name>; например: open story.txt\n")
                .append("  - информация о файле: info\n")
                .append("  - закрыть файл и выйти: quit\n")
                .append("  - отобразить страницу: page <number>\n")
                .append("  - переход на страницу вперед: next\n")
                .append("  - переход на страницу назад: prev\n")
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
                        quantityPages = 1 + (int) (file.length() / PAGE_SIZE);
                        showPage(currentPage);
                    } else {
                        System.out.println("Файл не найден.");
                    }
                    break;
                case "page":
                    currentPage = Integer.parseInt(userInput.split(" ")[1]);
                    showPage(currentPage);
                    break;
                case "next":
                    ++currentPage;
                    if (currentPage <= quantityPages)
                        showPage(currentPage);
                    else
                        System.out.println("Нет такой страницы. Конец книги.");
                    break;
                case "prev":
                    --currentPage;
                    if (currentPage > 1)
                        showPage(currentPage);
                    else {
                        System.out.println("Вы уже на первой странице книги.");
                        currentPage = 1;
                    }
                    break;
                case "quit":
                    if (randomAccessFile != null)
                        randomAccessFile.close();
                    whileIsTerminated = true;
                    break;
                case "help":
                    System.out.println(help);
                    break;
                case "info":
                    if (randomAccessFile != null) {
                        StringBuffer info = new StringBuffer();
                        info.append(" Размер файла, байт: ").append(file.length()).append("\n")
                                .append(" Страница содержит, символов: ").append(PAGE_SIZE).append("\n")
                                .append(" Всего страниц: ").append(quantityPages);
                        System.out.println(info);
                    } else
                        System.out.println("Файл не открыт.");
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
        /* Инициализация буфера */
        byte[] buffer = new byte[PAGE_SIZE];

        /* Начальная позиция чтения из файла */
        if (pageNumber == 1) randomAccessFile.seek(pageNumber - 1);
        else randomAccessFile.seek((pageNumber - 1) * PAGE_SIZE);

        /* Чтение в byte[]-буфер */
        randomAccessFile.read(buffer);

        /* Преобразуем byte[]-буфер в строку символов UFT-8 для вывода на экран */
        System.out.println(new String(buffer, "UTF-8"));
        System.out.println("\n_____________________________________________________________ Page # " + pageNumber);
    }
}