package ru.davidlevy.lesson3.homework;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Task1 {
    public static void main(String[] args) throws IOException {
        String workDir = "src/ru/davidlevy/lesson3/homework/";
        String filename = "Task1.temp";

        System.out.println("Прочитать файл (около 50 байт) в байтовый массив и вывести этот массив в консоль");

        /* Создание файла */
        try (FileOutputStream fileOutputStream = new FileOutputStream(workDir + filename)) {
            for (int i = 65; i < 115; i++)
                fileOutputStream.write(i);
        }

        /* Буфер */
        byte[] buffer = null;

        /* Чтение файла */
        try (FileInputStream fileInputStream = new FileInputStream(workDir + filename)) {
            buffer = new byte[fileInputStream.available()];
            fileInputStream.read(buffer);
        }

        System.out.println(" Результат byte[] " + Arrays.toString(buffer));
    }
}