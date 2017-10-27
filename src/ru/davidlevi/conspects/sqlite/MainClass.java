package ru.davidlevi.conspects.sqlite;

import java.sql.*;

/**
 * База данных. SQLite
 * <p>
 * - Руководство по JDBC http://www.w3ii.com/jdbc/jdbc_statements.html
 * - SQL Injection https://www.w3schools.com/sql/sql_injection.asp
 * - Профилактика SQL-инъекций https://habrahabr.ru/post/87872/
 * - Обработка объектов CallableStatement с использованием именованных параметров https://www.ibm.com/developerworks/ru/library/dm-0802tiwary/index.html
 */
public class MainClass {
    /* Точка входа */
    public static void main(String[] args) throws SQLException {
        /* Подключение */
        Sqlite.connect();

        /* Заявляем CRUD-запрос
        * НЕ ИСПОЛЬЗОВАТЬ ИЗ-ЗА АТАК SQL-INJECTION!
        */
        //Sqlite.statement();

        /* Подготовленное заявление для CRUD-запроса */
        Sqlite.preparedStatement();

        /* Вызываемое заявление для вызова хранимой процедуры, расположенной на сервере */
        // Читай здесь http://www.javaportal.ru/java/tutorial/tutorialJDBC/callablestatement.html
        Sqlite.callableStatement();

        /* Точка сохранения */
        Sqlite.savepoint();

        /* Обработка данных большого объема */
        Sqlite.indexing();

        /* Матаданные */
        Sqlite.meta();

        /* Отключение */
        Sqlite.disconnect();
    }
}