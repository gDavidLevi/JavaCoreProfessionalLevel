package ru.davidlevi.conspects.sqlite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.*;

class Sqlite {
    /* Рабочий путь */
    private static final String WORK_PATH = "src/ru/davidlevi/conspects/sqlite/";

    /* Путь к файлу БД */
    private static final String URL = "jdbc:sqlite:" + WORK_PATH + "database.sqlite";

    /* Интерфейс Connection */
    private static Connection connection;

    /* Интерфейс заявления для выполнения CRUD-операций. НЕ ИСПОЛЬЗОВАТЬ ИЗ-ЗА ОПАСНОСТИ SQL-ИНЪЕКЦИЙ! */
    private static Statement statement;

    /* Интерфейс подготовленного заявления для выполнения CRUD-операций */
    private static PreparedStatement preparedStatement;

    /**
     * Подключение к БД
     */
    static void connect() {
        try {
            /* Логирование */
            PrintWriter outLogInFile = new PrintWriter(new File(WORK_PATH + "DriverManager.log"), "UTF-8");
            DriverManager.setLogWriter(new PrintWriter(outLogInFile));

            /* Подключение */
            connection = DriverManager.getConnection(URL);
        } catch (SQLException | FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Statement
     *
     * @throws SQLException ошибка
     */
    static void statement() throws SQLException {
        /* Создать заявление */
        statement = connection.createStatement();

        /* CREATE */
        statement.execute("CREATE TABLE IF NOT EXISTS Students (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, score INTEGER);");
        /* Очистим таблицу перед добавлением */
        statement.execute("DELETE FROM Students;");
        /* Очистить счетчик автоинкремента */
        statement.execute("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='Students';");

        /* INSERT */
        int resultOfInsert = statement.executeUpdate("INSERT INTO Students (name, score) VALUES ('David',100),('Sarah',200),('Alex',50), ('Max',50);");
        System.out.println("Вставлено, строк: " + resultOfInsert);

        /* INSERT */
        for (int i = 0; i < 6; i++)
            statement.executeUpdate("INSERT INTO Students (name, score) VALUES ('Ben" + i + "',1000);");

        /* SELECT */
        /* В заявлении написать SQL-запрос на выполнение */
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Students WHERE score > 0;");
        /* Получить наборы значений пока имееются */
        while (resultSet.next()) {
            /* Извлечь из набора конкретные значения */
            System.out.println(resultSet.getInt("id") + " " + resultSet.getString("name") + " " + resultSet.getInt("score"));
        }

        /* DELETE */
        int resultOfDelete = statement.executeUpdate("DELETE FROM Students WHERE score = 50;");
        System.out.println("Удалено, строк: " + resultOfDelete);
    }

    /**
     * PreparedStatement
     *
     * @throws SQLException ошибка
     */
    static void preparedStatement() throws SQLException {
        /* Мы заранее знаем, что будет много запросов.
        * Для подавления флудинга мы отключаем автокоммит для комманды executeUpdate(), которая выполняет КОММИТ */
        connection.setAutoCommit(false);

        /* DROP TABLE IF EXISTS... */
        preparedStatement = connection.prepareStatement("DROP TABLE IF EXISTS Students;");
        preparedStatement.executeUpdate();

        /* CREATE TABLE IF NOT EXISTS... */
        preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Students (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, score INTEGER);");
        preparedStatement.executeUpdate();

        /* CREATE INDEX */
        /* Для ускорения обработки (вставки, обновления...) большого количества данных */
        preparedStatement = connection.prepareStatement("CREATE INDEX Students_id_name_score_index ON Students (name, score);");
        preparedStatement.executeUpdate();

        /* Очистим таблицу перед добавлением */
        preparedStatement = connection.prepareStatement("DELETE FROM Students;");
        preparedStatement.executeUpdate();

        /* Очистить счетчик автоинкремента */
        preparedStatement = connection.prepareStatement("UPDATE SQLITE_SEQUENCE SET seq=0 WHERE name='Students';");
        preparedStatement.executeUpdate();

        /* INSERT */
        preparedStatement = connection.prepareStatement("INSERT INTO Students (name, score) VALUES ('David',100),('Sarah',200),('Alex',50),('Max',50);");
        int resultInsert = preparedStatement.executeUpdate();
        System.out.println("Вставлено, строк: " + resultInsert);

        /* INSERT Batch*/
        preparedStatement = connection.prepareStatement("INSERT INTO Students (name, score) VALUES (?,?);");
        for (int i = 0; i < 6; i++) {
            preparedStatement.setString(1, "Ben" + i);
            preparedStatement.setInt(2, i);
            preparedStatement.addBatch();
        }
        int[] resultInsertBatch = preparedStatement.executeBatch();
        //connection.commit(); // После executeBatch() обычно следует connection.commit();, но мы выполним его позже.
        System.out.println("Вставлено[], строк: " + resultInsertBatch.length);

        /* SELECT */
        preparedStatement = connection.prepareStatement("SELECT * FROM Students WHERE score > 0;");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getInt("id") + " " + resultSet.getString("name") + " " + resultSet.getInt("score"));
        }

        /* Коммиттим все запросы в один и запускаем executeUpdate() */
        connection.commit();

        /* Включим автокоммит для удаления */
        connection.setAutoCommit(true);

        /* DELETE WHERE score = 50 */
        preparedStatement = connection.prepareStatement("DELETE FROM Students WHERE score = 50;");
        preparedStatement.executeUpdate();
    }

    /**
     * CallableStatement (вызываемый оператор)
     *
     * @throws SQLException ошибка
     */
    static void callableStatement() throws SQLException {
        /**
         * Читай здесь http://www.javaportal.ru/java/tutorial/tutorialJDBC/callablestatement.html
         */
    }

    /**
     * Savepoint
     *
     * @throws SQLException ошибка
     */
    static void savepoint() throws SQLException {
        /* Мы выполняем некий запрос */
        preparedStatement = connection.prepareStatement("INSERT INTO Students (name, score) VALUES (?,?);");

        /* Объявляем точку сохранения */
        Savepoint savepoint = connection.setSavepoint("Savepoint1");

        /* Сделали изменение 1 */
        preparedStatement.setString(1, "A");
        preparedStatement.setInt(2, 1);
        preparedStatement.executeUpdate();

        /* Сделали изменение 2 */
        preparedStatement.setString(1, "B");
        preparedStatement.setInt(2, 2);
        preparedStatement.executeUpdate();

        /* Возникла некая ошибка в процессе вставки изменений 1 и/или 2*/
        boolean error = true;
        /* Обработаем ошибку. Сделаем откат транзацкции до точки сохранения savepoint */
        if (error)
            connection.rollback(savepoint);

        preparedStatement.setString(1, "rollback");
        preparedStatement.setInt(2, 3);
        preparedStatement.executeUpdate();

        /* Отправим на выполнение */
        connection.commit();
    }

    /**
     * Обработка большого объема данных. Индексация
     *
     * @throws SQLException ошибка
     */
    static void indexing() throws SQLException {
        /* Индексация
        * После создания таблицы необходимо построить индексы, чтобы быстрее происходил доступ к данным БД
        * preparedStatement = connection.prepareStatement("CREATE INDEX Students_id_name_score_index ON Students (name, score);");
        * preparedStatement.executeUpdate();
        */

        /* Выключаем AutoCommit, чтобы не флудить */
        connection.setAutoCommit(false);

        /* INSERT INTO Students... */
        long start = System.currentTimeMillis();
        preparedStatement = connection.prepareStatement("INSERT INTO Students (name, score) VALUES (?,?);");
        for (int i = 0; i < 100000; i++) {
            preparedStatement.setString(1, "Bob" + i);
            preparedStatement.setInt(2, i);
            preparedStatement.addBatch();
        }
        int[] resultInsert = preparedStatement.executeBatch();
        connection.commit(); // Подтвердить выполнение
        System.out.println("For " + resultInsert.length + " records the time of INSERT, ms: " + (System.currentTimeMillis() - start)); // 1727 ms

        /* UPDATE Students SET... */
        /* До индексации данные обновлялись очень-очень долго */
        start = System.currentTimeMillis();
        preparedStatement = connection.prepareStatement("UPDATE Students SET score = 0 WHERE name = ?;");
        for (int i = 0; i < 100000; i++) {
            preparedStatement.setString(1, "Bob" + i);
            preparedStatement.addBatch();
        }
        int[] resultUpdate = preparedStatement.executeBatch();
        connection.commit(); // Подтвердить выполнение
        System.out.println("For " + resultUpdate.length + " records the time of UPDATE, ms: " + (System.currentTimeMillis() - start)); // 1956 ms
    }

    /**
     * Метаданные
     *
     * @throws SQLException ошибка
     */
    static void meta() throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        System.out.println("MetaData: ");
        if (databaseMetaData.supportsNamedParameters())
            System.out.println(" Именованные параметры поддерживаются.");
        if (databaseMetaData.supportsBatchUpdates())
            System.out.println(" Поддерживает пакетные обновления.");
        System.out.println(" База данных только для чтения? " + databaseMetaData.isReadOnly());

    }

    /**
     * Закрытие подключения к БД
     *
     * @throws SQLException ошибка
     */
    static void disconnect() throws SQLException {
        connection.close();
    }
}