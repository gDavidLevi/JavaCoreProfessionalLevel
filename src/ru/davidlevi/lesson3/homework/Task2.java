package ru.davidlevi.lesson3.homework;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class Task2 {
    public static void main(String[] args) throws IOException {
        System.out.println("Последовательно сшить 5 файлов в один (файлы также ~100 байт)");

        String workDir = "src/ru/davidlevi/lesson3/homework/";
        String outFilename = "task2_all.temp";

        /* Сгенерируем имена файлов */
        ArrayList<String> files = new ArrayList<>();
        for (int i = 0; i < 5; i++) files.add("task2_" + i + ".temp");
        /*
        IntStream.range(1, 5).forEach(element -> files.insert("task2_" + element + ".temp"));
        */

        /* Создание файлов */
        for (String oneFile : files) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(workDir + oneFile)) {
                for (int i = 0; i < 100; i++)
                    fileOutputStream.write(randomFromRange(65, 89));
                fileOutputStream.write('\n');
            }
        }

        /* Массив из файловых входящих потоков */
        ArrayList<FileInputStream> fileInputStreamArrayList = new ArrayList<>();

        /* Инициируем массив потками */
        for (String oneFile : files)
            fileInputStreamArrayList.add(new FileInputStream(workDir + oneFile));

        /* Создадим перечисление на базе массива */
        Enumeration<FileInputStream> fileInputStreamEnumeration = Collections.enumeration(fileInputStreamArrayList);

        /* Создадим последовательность из перечисления */
        SequenceInputStream sequenceInputStream = new SequenceInputStream(fileInputStreamEnumeration);

        /* Создадим исходящий файловый поток и запишем всю последовательность */
        try (FileOutputStream fileOutputStream = new FileOutputStream(workDir + outFilename)) {
            int oneByte;
            while ((oneByte = sequenceInputStream.read()) != -1)
                fileOutputStream.write(oneByte);
        }

        /* Информация о файле */
        File file = new File(workDir + outFilename);
        if (file.exists())
            System.out.printf(" Размер выходного файла %s составляет %d байт.%n", outFilename, file.length());
    }

    /**
     * Метод возвращает случайное чисто из диапазона включительно
     *
     * @param min минимальное
     * @param max максимальное
     * @return int
     */
    private static int randomFromRange(int min, int max) {
        max -= min;
        return (int) ((Math.random() * ++max) + min);
    }
}