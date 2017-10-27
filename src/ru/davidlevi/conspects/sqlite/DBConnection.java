package ru.davidlevi.conspects.sqlite;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

class DBConnection {
    /* SQLite */
    //private static final String JDBC_DRIVER = "org.sqlite.JDBC";
    //private static final String URL = "jdbc:sqlite:src/dbname.sqlite";

    /* MySQL */
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/dbname";

    /* MariaDB */
    //private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    //private static final String URL = "jdbc:mariadb://127.0.0.1:3306/dbname";

    /* PostgreSQL */
    //private static final String JDBC_DRIVER = "org.postgresql.Driver";
    //private static final String URL = "jdbc:postgresql://127.0.0.1:5432/dbname";

    /* MS SQL Server - SQL authentication */
    //private static final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    //private static final String URL = "jdbc:sqlserver://127.0.0.1\\SQLEXPRESS;databaseName=dbname";
    //Connection connection = DriverManager.getConnection(url, userName, password);
    // after adding sqljdbc4.jar to the build path.

    /* MS SQL Server - Window authentication */
    //private static final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    //private static final String URL = "jdbc:sqlserver://127.0.0.1\\SQLEXPRESS;databaseName=dbname;integratedSecurity=true";
    //Connection connection = DriverManager.getConnection(url);
    // after add the path to sqljdbc_auth.dll as a VM argument (still need sqljdbc4.jar in the build path).

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private static Connection connection;
    private static final String WORK_PATH = "src/ru/davidlevi/conspects/sqlite/";

    static void loadSettingsFromFileProperties() throws IOException, SQLException, ClassNotFoundException {
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(WORK_PATH + "database.properties");

        properties.load(inputStream);

        String driver = properties.getProperty("jdbc.driver");
        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");

        /* Логирование событий JDBC. Выполнить до подключения! */
        PrintWriter outLogInFile = new PrintWriter(new File(WORK_PATH + "DBConnection.log"), "UTF-8");
        DriverManager.setLogWriter(outLogInFile);

        /* Принудительно загрузить класс драйвера */
        Class.forName(driver); // Данный код начиная с Java 7 можно не использовать
        connection = DriverManager.getConnection(url, username, password);
    }

    static void connection() {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            /* Statement statement = connection.createStatement(); */
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void disconnect() throws SQLException {
        connection.close();
    }
}