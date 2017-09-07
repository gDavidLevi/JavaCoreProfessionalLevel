package ru.davidlevy.lesson7.classwork;

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
@XTable(tableName = "Students")
class Students {
    @XField
    String name;

    @XField
    String sex;

    @XField(fieldName = "grp")
    String group;

    @XField
    int score;

    @XField
    int course;

    String otherFld;
}