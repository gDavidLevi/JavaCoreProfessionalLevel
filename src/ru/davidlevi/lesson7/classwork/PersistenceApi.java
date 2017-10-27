package ru.davidlevi.lesson7.classwork;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * #see Java Persistence API (JPA)
 */
public class PersistenceApi {
    /*
    CREATE TABLE Students (
    ID    INTEGER PRIMARY KEY AUTOINCREMENT,
    NAME  TEXT,
    SCORE INTEGER
    );
    */
    public static void main(String[] args) throws Exception {
        /* Словарь соответствия типов java и sqlite */
        HashMap<Class, String> hm = new HashMap<>();
        hm.put(int.class, "Integer");
        hm.put(String.class, "TEXT");

        /* Из класса Student получим массив полей */
        Class c = Students.class;
        Field[] fields = c.getDeclaredFields();

        /* Подключимся к БД */
        String workPath = "src/ru/davidlevi/lesson7/classwork/";
        String url = "jdbc:sqlite:" + workPath + "database.sqlite";
        Connection connection = DriverManager.getConnection(url);

        /* Начнем создавать sql-запрос для создания таблицы */
        String sql = "CREATE TABLE "
                + ((XTable) c.getAnnotation(XTable.class)).tableName()
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, ";


        /* Сформируем из полей fields класса Students аннотированных XField список fieldAndType */
        List<String> fieldAndType = new ArrayList<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(XField.class)) {
                String fieldName = field.getAnnotation(XField.class).fieldName();
                if (fieldName.isEmpty()) {
                    fieldName = field.getName();
                }
                /* Добавим в список fieldAndType имя поля из класса Student и приведенный тип данных */
                fieldAndType.add(fieldName + " " + hm.get(field.getType()));
            }
        }
        /* Преобразуем список в строку с разделителем "," */
        String strFieldsAndTypes = String.join(", ", fieldAndType);

        /* Собирем строку запроса */
        sql += strFieldsAndTypes + ");";

        System.out.println(sql);
        /*
        CREATE TABLE Students(
            ID INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT,
            sex TEXT,
            grp TEXT,
            score Integer,
            course Integer
        );
        */

        /* Выполним sql-запрос */
        Statement stmt = connection.createStatement();
        stmt.execute(sql);
        connection.close();
    }
}