# Аннотации

Java-аннотация — в языке Java специальная форма синтаксических метаданных, которая может быть добавлена в исходный код.

Аннотации используются для анализа кода, компиляции или выполнения. Аннотируемы пакеты, классы, методы, переменные и параметры.

Выглядит как @ИмяАннотации, предваряющее определение переменной, параметра, метода, класса, пакета.

Аннотация выполняет следующие функции:
* даёт необходимую информацию для компилятора;
* даёт информацию различным инструментам для генерации другого кода, конфигураций и т. д.;
* может использоваться во время выполнения для получения данных через отражение (reflection);

Аннотации, применяемые к исходному коду:
* @Override — проверяет, переопределён ли метод. Вызывает ошибку компиляции, если метод не найден в родительском классе или интерфейсе;
* @Deprecated — отмечает, что метод устарел и не рекомендуется к использованию. Предполагается, что по каким-то причинам этот метод пока оставлен, но будет удалён в будущих версиях. Вызывает предупреждение компиляции, если метод используется;
* @SuppressWarnings — указывает компилятору подавить предупреждения компиляции, определённые в параметрах аннотации;
* @SafeVarargs —  указывает, что никакие небезопасные действия, связанные с параметром переменного количества аргументов, недопустимы. Применяется только к методам и конструкторам с переменным количеством аргументов, которые объявлены как static или final.

Аннотации, применяемые к другим аннотациям:
* @Retention — позволяет указать жизненный цикл аннотации: будет она присутствовать только в исходном коде, в скомпилированном файле, или она будет также видна и в процессе выполнения. Выбор нужного типа зависит от того, как вы хотите использовать аннотацию, например, генерировать что-то побочное из исходных кодов, или в процессе выполнения стучаться к классу через reflection. 
* @Documented — указывает, что помеченная таким образом аннотация должна быть добавлена в javadoc поля/метода и т.д.
* @Target — указывает, что именно мы можем пометить этой аннотацией, это может быть поле, метод, тип и т.д.
* @Inherited — помечает аннотацию, которая будет унаследована потомком класса, отмеченного такой аннотацией.

Аннотация задается описанием соответствующего интерфейса. 
```java 
public @interface MyAnnotation {
  
}
```
Область видимости для аннотации – сообщает где будет использовать аннотация:
* @Retention(RetentionPolicy.RUNTIME) - компилируется и видна во время исполнения
* @Retention(RetentionPolicy.CLASS) - компилируется, но не видна в момент исполнения
* @Retention(RetentionPolicy.SOURCE) - не компилируется и не видна в момент исполнения

Цель назначения – к какому типу данных можно подключить эту аннотацию:
* @Target(ElementType.PACKAGE) — только для пакетов;
* @Target(ElementType.TYPE) — только для классов;
* @Target(ElementType.CONSTRUCTOR) — только для конструкторов;
* @Target(ElementType.METHOD) — только для методов;
* @Target(ElementType.FIELD) — только для атрибутов(переменных) класса;
* @Target(ElementType.PARAMATER) — только для параметров метода;
* @Target(ElementType.LOCAL_VARIABLE) — только для локальных переменных.
* @Target({ ElementType.PARAMETER, ElementType.LOCAL_VARIABLE })

Выполнить метод помеченный аннотацией:
```java 
public class MainClass {
   public static void main(String[] args) throws Exception {
       Class mc = DavidClass.class;
       Method[] methods = mc.getDeclaredMethods();
       for (Method o : methods) {
           if (o.isAnnotationPresent(MyAnnotation.class)) { // выполнить invoke если метод помечен аннотацией
               o.invoke(null);
           }
       }
   }
}

class DavidClass {
   @MyAnnotation
   static void a() {
       System.out.println("a");
   }

   static void b() {
       System.out.println("b");
   }

   @MyAnnotation
   static void c() {
       System.out.println("c");
   }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface MyAnnotation {

}
```
```text 
____________________
c
a

Process finished with exit code 0
```

Значение у аннотаций, получение этих значений:
```java 
public class MainClass {
   public static void main(String[] args) throws Exception {
       Class mc = DavidClass.class;
       Method[] methods = mc.getDeclaredMethods();
       for (Method o : methods) {
           if (o.isAnnotationPresent(DavidAnnotation.class)) {
               o.invoke(null); // null если метод без параметров
               System.out.println(o.getAnnotation(DavidAnnotation.class).anValue());
               System.out.println(o.getAnnotation(DavidAnnotation.class).data());
           }
       }
       // Получим:
       // с
       // 20
       // a
       // 10
       // 60
   }
}

class DavidClass {
   @DavidAnnotation(anValue = 10, data = 60) // значение
   static void a() {
       System.out.println("a");
   }

   static void b() {
       System.out.println("b");
   }

   @DavidAnnotation(anValue = 20) // значение
   static void c() {
       System.out.println("c");
   }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface DavidAnnotation {
   //int anValue();  // абстрактный метод возвращающий значение. Задали обязательность ввода значения для наше аннотации
   int anValue() default 12;  // так указываем значение по умолчанию
   int data() default 4;  
}
```

Как подгрузить класс к нашему проекту на ходу:
Замечание! Используется для создании системы плагинов.
Есть код:
```java 
public class Unknown{
   int x;
   int y;

   public Unknown(int x, int y) {
       this.x = x;
       this.y = y;
   }

   public void doSomething() {
       System.out.println("Unknown class method: " + this.x + " " + this.y);
   }
}
```
Скомпилируем наш класс, получим Unknown.class:
c:\> set path="C:\Program Files\Java\jdk1.8.0_131\bin"
c:\> javac Unknown.java

Прикрепим скомпилированный код к нашему проекту:
```java 
public class MainClass {
   public static void main(String[] args) throws Exception {
       URL[] folderURLs = {new URL("file:/c:/")};
       // Найти класс Unknown в папке c:\
       Class c = URLClassLoader.newInstance(folderURLs).loadClass("Unknown");

       // Создаем объект
       Object o = c.getConstructor(int.class, int.class).newInstance(5, 5);
       // Получаем метод
       Method m = c.getMethod("doSomething");
       // Запускаем метод
       m.invoke(o);
   }
}
```
```text 
________________
Unknown class method: 5 5

Process finished with exit code 0
```
Создадим на базе аннотированного класса Student таблицу в БД:
```java 
public class MainClass {
   public static void main(String[] args) throws Exception {
       Class c = Student.class;

       // для создания соответствия с БД
       HashMap<Class, String> hm = new HashMap<Class, String>();
       hm.put(int.class, "Integer");
       hm.put(String.class, "TEXT");

       Connection connection = DriverManager.getConnection("jdbc:sqlite:temp.db");

       // Создание строки создания таблицы:
       // - создать таблицу Students
       String x = "CREATE TABLE " + ((XTable) c.getAnnotation(XTable.class)).tableName() + "(";
       // - создаем поля
       Field[] fields = c.getDeclaredFields();  // получаем массив полей их класса Students
       for (int i = 0; i < fields.length; i++) {
           // - если поле в классе аннотировано, то будем создавать это поле в БД
           if (fields[i].isAnnotationPresent(XField.class)) {
               // - ИМЯ_ПОЛЯ + ТИП из HashMap (то есть если это int-поле, то указываем тип Integer)
               x += fields[i].getName() + " " + hm.get(fields[i].getType()) + ", ";
           }
       }
       // - удаляем ", "
       x = x.substring(0, x.length() - 2);
       // - закрываем скрипт
       x += ");";

       System.out.println(x); // CREATE TABLE Students(name TEXT, sex TEXT, grp TEXT, cource Integer, score Integer);

       Statement stmt = connection.createStatement();
       stmt.execute(x);
       connection.close();
   }
}

@XTable(tableName = "Students")
class Student {
   @XField
   String name;

   @XField
   String sex;

   @XField
   String grp;

   @XField
   int cource;

   @XField
   int score;
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface XField {
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface XTable {
   String tableName();
}
```
