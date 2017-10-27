package ru.davidlevi.lesson2.homework;

import java.sql.*;
import java.util.Scanner;

public class MainClass {
    /* Точка входа */
    public static void main(String[] args) {
        try {
            /* Инициализация */
            init();
            /* Слушатель комманд */
            listener();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* URL базы данных */
    private static final String URL = "jdbc:sqlite:src/ru/davidlevi/lesson2/homework/database.sqlite";

    /* Интерфейсы для работы с БД */
    private static Connection connection;
    private static PreparedStatement preparedStatement;

    /* Подключение к БД */
    private static void connect() throws SQLException {
        connection = DriverManager.getConnection(URL);
    }

    /* Отключение от БД */
    private static void disconnect() throws SQLException {
        connection.close();
    }

    /* Инициализация БД, таблицы, первичных данных */
    private static void init() throws SQLException {
        connect();
        connection.setAutoCommit(false);
        preparedStatement = connection.prepareStatement("DROP TABLE IF EXISTS Articles;");
        preparedStatement.executeUpdate();
        preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Articles (id INTEGER PRIMARY KEY AUTOINCREMENT, prodid INTEGER, title TEXT, cost INTEGER);");
        preparedStatement.executeUpdate();
        preparedStatement = connection.prepareStatement("CREATE INDEX Articles_title_cost_index ON Articles (title, cost);");
        preparedStatement.executeUpdate();
        preparedStatement = connection.prepareStatement("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='Articles';");
        preparedStatement.executeUpdate();
        preparedStatement = connection.prepareStatement("INSERT INTO Articles (prodid, title, cost) VALUES (?,?,?);");
        for (int i = 1; i <= 10000; i++) {
            preparedStatement.setInt(1, i);
            preparedStatement.setString(2, "article" + i);
            preparedStatement.setInt(3, i * 10);
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
        connection.commit();
        disconnect();
    }

    /* Цикл ввода комманд с консоли */
    private static void listener() throws SQLException {
        String help = "Доступные комманды: \n" +
                "/exit \t\t\t\t\t- завершение работы\n" +
                "/price ТОВАР \t\t\t- цена ТОВАРА, например: /price article30 \n" +
                "/setprice ТОВАР ЦЕНА \t- изменить ЦЕНУ ТОВАРА, например: /setprice article30 12345 \n" +
                "/productsbyprice C ПО\t- показать ТОВАРЫ по цене в диапазоне С ПО, например: /productsbyprice 100 300\n";
        System.out.println(help);
        System.out.println("Наберите комманду!");
        Scanner scanner = new Scanner(System.in, "UTF-8");
        String command;
        while (true) {
            command = scanner.nextLine().toLowerCase().trim();
            if (command.equals("/exit"))
                break;
            else
                handler(command);
        }
        scanner.close();
    }

    /**
     * Обработчик комманд
     *
     * @param command String
     */
    private static void handler(String command) throws SQLException {
        String[] parts = command.split(" ");
        if (parts.length != 0) {
            connect();
            ResultSet resultSet;
            switch (parts[0]) {
                case "/price":
                    preparedStatement = connection.prepareStatement("SELECT cost FROM Articles WHERE title = ?");
                    preparedStatement.setString(1, parts[1]);
                    resultSet = preparedStatement.executeQuery();
                    if (resultSet.next())
                        System.out.println("Стоимость: " + resultSet.getInt("cost"));
                    else
                        System.out.println("Такого товара нет.");
                    break;
                case "/setprice":
                    preparedStatement = connection.prepareStatement("UPDATE Articles SET cost = ? WHERE title = ?;");
                    preparedStatement.setInt(1, Integer.parseInt(parts[2]));
                    preparedStatement.setString(2, parts[1]);
                    int countRows = preparedStatement.executeUpdate();
                    System.out.println("Обновлено, строк: " + countRows);
                    break;
                case "/productsbyprice":
                    preparedStatement = connection.prepareStatement("SELECT * FROM Articles WHERE cost >= ? AND cost <= ?;");
                    preparedStatement.setInt(1, Integer.parseInt(parts[1]));
                    preparedStatement.setInt(2, Integer.parseInt(parts[2]));
                    resultSet = preparedStatement.executeQuery();
                    System.out.printf("%6s %10s %6s%n", "prodid", "title", "cost");
                    while (resultSet.next())
                        System.out.printf("%6d %10s %6d%n", resultSet.getInt("prodid"), resultSet.getString("title"), resultSet.getInt("cost"));
                    break;
                default:
                    System.out.println("Неверная комманда.");
                    break;
            }
            disconnect();
        }
    }
}