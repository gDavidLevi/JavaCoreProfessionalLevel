package ru.davidlevi.lesson6.homework;

import org.junit.*;

import java.sql.*;

public class Task3Test {
    private static final String DATABASE_NAME = "src/ru/davidlevi/lesson6/homework/database.sqlite";
    private static Connection connection;
    private static PreparedStatement preparedStatement;

    /* Подключение к БД */
    private void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);
    }

    /* Отключение от БД */
    private void disconnect() throws SQLException {
        connection.close();
    }

    /* Конструктор */
    public Task3Test() throws SQLException {
        connect();

        /* Создание таблицы для тестирования */
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("DROP TABLE IF EXISTS Students;");
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Students (id INTEGER PRIMARY KEY AUTOINCREMENT, family TEXT, ball INTEGER);");
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("UPDATE SQLITE_SEQUENCE SET seq=0 WHERE name='Students';");
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("INSERT INTO Students (family, ball) VALUES (?,?);");
            for (int i = 1; i <= 5; i++) {
                preparedStatement.setString(1, "Family" + i);
                preparedStatement.setInt(2, i * (int) (Math.random() * 5));
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        disconnect();
    }

    /**
     * Метод insert добавляет запись в базу данных
     *
     * @param family фамилия
     * @param ball   балл
     * @return int возвращает количество добавленных записей
     */
    private int insert(String family, int ball) throws SQLException {
        return exec("INSERT", family, String.valueOf(ball));
    }

    /**
     * Метод update обновляет запись в базе данных
     *
     * @param family  для фамилии
     * @param setBall устанавливает балл
     * @return int возвращает количество обновленных записей
     */
    private int update(String family, int setBall) throws SQLException {
        return exec("UPDATE", family, String.valueOf(setBall));
    }

    /**
     * Метод delete уладяет запись в базе данных
     *
     * @param family для фамилии
     * @return int возвращает количество удаленных записей
     */
    private int delete(String family) throws SQLException {
        return exec("DELETE", family);
    }

    private int exec(String request, String... param) throws SQLException {
        connection.setAutoCommit(false);
        int answer = -1;
        switch (request) {
            case "INSERT":
                preparedStatement = connection.prepareStatement("INSERT INTO Students (family, ball) VALUES (?,?);");
                preparedStatement.setString(1, param[0]);
                preparedStatement.setInt(2, Integer.parseInt(param[1]));
                break;
            case "UPDATE":
                preparedStatement = connection.prepareStatement("UPDATE Students SET ball = ? WHERE family = ?;");
                preparedStatement.setInt(1, Integer.parseInt(param[1]));
                preparedStatement.setString(2, param[0]);
                break;
            case "DELETE":
                preparedStatement = connection.prepareStatement("DELETE FROM Students WHERE family = ?;");
                preparedStatement.setString(1, param[0]);
            default:
                break;
        }
        answer = preparedStatement.executeUpdate();
        return answer;
    }

    /* Блок тестирования: */

    private static Task3Test test;

    @Before
    public void startTest() throws SQLException {
        test = new Task3Test();
        test.connect();
    }

    @Test
    public void testAdd() throws SQLException {
        Assert.assertEquals(1, test.insert("Igor", 17));
    }

    @Test
    public void testUpdate() throws SQLException {
        test.insert("Igor", 17);
        Assert.assertEquals(1, test.update("Igor", 17000));
    }

    @Test
    public void testDelete() throws SQLException {
        test.insert("Igor", 17);
        Assert.assertEquals(1, test.delete("Igor"));
    }

    @After
    public void endTest() throws SQLException {
        test.disconnect();
    }
}