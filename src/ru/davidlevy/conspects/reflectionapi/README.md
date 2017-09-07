# Reflect API, загрузка классов в Java

Рефлексия (лат. reflexio - обращение назад) - это механизм исследования данных о программе во время её выполнения. Рефлексия позволяет исследовать информацию о полях, методах и конструкторах классов. Можно также выполнять операции над полями и методами которые исследуются. Рефлексия в Java осуществляется с помощью Java Reflection API. Этот интерфейс API состоит из классов пакетов java.lang и java.lang.reflect. С помощью интерфейса Java Reflection API можно делать следующее: 

* Определить класс объекта. 
* Получить информацию о модификаторах класса, полях, методах, конструкторах и суперклассах.
* Выяснить, какие константы и методы принадлежат интерфейсу. 
* Создать экземпляр класса, имя которого неизвестно до момента выполнения программы.
* Получить и установить значение свойства объекта.
* Вызвать метод объекта.
* Создать новый массив, размер и тип компонентов которого неизвестны до момента выполнения программ.


#### Получение объекта типа Class
```java 
MyClass a = new MyClass(); 
Class aclass = a.getClass(); 
```
Самое простое, что обычно делается в динамическом программировании, - это получают объект типа java.lang.Class. Если у нас есть экземпляр объекта Class мы можем получить всевозможную информацию об этом классе и даже осуществлять операции над ним. Вышеприведенный метод getClass() часто полезен тогда когда есть экземпляр объекта, но не известно какого класса этот экземпляр. Если у нас есть класс, для которого в момент компиляции известен тип, то получить экземпляр класса ещё проще.
```java 
Class aclass = MyClass.class; 
Class iclass = Integer.class; 
```
Если имя класса не известно в момент компиляции, но становится известным во время выполнения программы, можно использовать метод forName(), чтобы получить объект Class.
```java 
Class c = Class.forName("com.mysql.jdbc.Driver");
```

#### Получение имени класса
```java 
Class c = myObject.getClass(); 
String s = c.getName(); 
```
Объект типа String, возвращаемый методом getName(), будет содержать полностью уточненное имя класса, т.е. если типом объекта myObject будет Integer, то результат будет вида java.lang.Integer .

#### Исследование модификаторов класса
```java 
Class c =  obj.getClass(); 
int mods = c.getModifiers(); 
if (Modifier.isPublic(mods)) { 
    System.out.println("public"); 
} 
if (Modifier.isAbstract(mods)) { 
    System.out.println("abstract"); 
} 
if (Modifier.isFinal(mods)) { 
    System.out.println("final"); 
} 
```
Чтобы узнать, какие модификаторы были применены к заданному классу, сначала нужно с помощью метода getClass получить объект типа Class, представляющий данный класс. Затем нужно вызвать метод getModifiers() для объекта типа Class, чтобы определить значение типа int, биты которого представляют модификаторы класса. После этого можно использовать статические методы класса java.lang.reflect.Modifier, чтобы определить, какие именно модификаторы были применены к классу.

#### Нахождение суперклассов
```java 
Class c = myObj.getClass(); 
Class superclass = c.getSuperclass(); 
```
Можно также использовать метод getSuperclass() для объекта Class, чтобы получить объект типа Class, представляющий суперкласс рефлексированного класса. Нужно не забывать учитывать, что в Java отсутствует множественное наследование и класс java.lang.Object является базовым классом для всех классов, вследствие чего если у класса нет родителя то метод getSuperclass вернет null. Для того чтобы получить все родительские суперклассы, нужно рекурсивно вызывать метод getSuperclass().

#### Определение интерфейсов, реализуемых классом
```java 
Class c =  LinkedList.class; 
Class[] interfaces = c.getInterfaces(); 
for(Class cInterface : interfaces) { 
    System.out.println( cInterface.getName() ); 
} 
```
С помощью рефлексии можно также определить, какие интерфейсы реализованы в заданном классе. Метод getInterfaces() вернет массив объектов типа Class. Каждый объект в массиве представляет один интерфейс, реализованный в заданном классе.

#### Исследование, получение и установка значений полей класса.
```java 
Class c = obj.getClass(); 
Field[] publicFields = c.getFields(); 
for (Field field : publicFields) { 
    Class fieldType = field.getType(); 
    System.out.println("Имя: " + field.getName()); 
    System.out.println("Тип: " + fieldType.getName()); 
} 
```
Чтобы исследовать поля принадлежащие классу, можно воспользоваться методом getFields() для объекта типа Class. Метод getFields() возвращает массив объектов типа java.lang.reflect.Field, соответствующих всем открытым полям объекта. Эти открытые поля необязательно должны содержаться непосредственно внутри класса, с которым вы работаете, они также могут содержатся в суперклассе, интерфейсе или интерфейсе представляющем собой расширение интерфейса, реализованного классом. С помощью класса Field можно получить имя поля, тип и модификаторы. Если известно имя поля, то можно получить о нем информацию с помощью метода getField()
```java 
Class c = obj.getClass(); 
Field nameField = c.getField("name");
``` 
Методы getField() и getFields() возвращают только открытые члены данных класса. Если требуется получить все поля некоторого класса нужно использовать методы getDeclaredField() и getDeclaredFields(). Эти методы работают точно также как их аналоги getField() и getFields(), за исключением того, что они возвращают все поля, включая закрытые и защищенные. Чтобы получить значение поля, нужно сначала получить для этого поля объект типа Field затем использовать метод get(). Метод принимает входным параметром ссылку на объект класса.
```java 
Class c = obj.getClass(); 
Field field = c.getField("name"); 
String nameValue = (String) field.get(obj) 
``` 
Так же у класса Field имеются специализированные методы для получения значений примитивных типов: getInt(), getFloat(), getByte() и др.. Для установки значения поля, используется метод set().
```java 
Class c = obj.getClass(); 
Field field = c.getField("name"); 
field.set(obj, "New name"); 
``` 
Для примитивных типов имеются методы setInt(), setFloat(), setByte() и др. Вопрос к читателю, можно ли изменить значение private поля? А private final? Ответ узнаем чуть позже.

#### Исследование конструкторов класса
```java 
Class c = obj.getClass(); 
Constructor[] constructors = c.getConstructors(); 
for (Constructor constructor : constructors) { 
    Class[] paramTypes = constructor.getParameterTypes(); 
    for (Class paramType : paramTypes) { 
        System.out.print(paramType.getName() + " "); 
    } 
    System.out.println(); 
} 
``` 
Чтобы получить информацию об открытых конструкторах класса, нужно вызвать метод getConstructors() для объекта Class. Этот метод возвращает массив объектов типа java.lang.reflect.Constructor. С помощью объекта Constructor можно затем получить имя конструктора, модификаторы, типы параметров и генерируемые исключения. Можно также получить по отдельному открытому конструктору, если известны типы его параметров.
```java 
Class[] paramTypes = new Class[] { String.class, int.class }; 
Constructor aConstrct = c.getConstructor(paramTypes); 
``` 
Методы getConstructor() и getConstructors() возвращают только открытые конструкторы. Если требуется получить все конструкторы класса, включая закрытые можно использовать методы getDeclaredConstructor() и getDeclaredConstructors() эти методы работают точно также, как их аналоги getConstructor() и getConstructors().

#### Исследование информации о методе, вызов метода.
```java 
Class c = obj.getClass(); 
Method[] methods = c.getMethods(); 
for (Method method : methods) { 
    System.out.println("Имя: " + method.getName()); 
    System.out.println("Возвращаемый тип: " + method.getReturnType().getName()); 
 
    Class[] paramTypes = method.getParameterTypes(); 
    System.out.print("Типы параметров: "); 
    for (Class paramType : paramTypes) { 
        System.out.print(" " + paramType.getName()); 
    } 
    System.out.println(); 
} 
``` 
Чтобы получить информацию об открытых методах класса, нужно вызвать метод getMethods() для объекта Class. Этот метод возвращает массив объектов типа java.lang.reflect.Method. Затем с помощью объекта Method можно получить имя метода, тип возвращаемого им значения, типы параметров, модификаторы и генерируемые исключения. Также можно получить информацию по отдельному методу если известны имя метода и типы параметров.
```java 
Class c = obj.getClass(); 
Class[] paramTypes = new Class[] { int.class, String.class}; 
Method m = c.getMethod("methodA", paramTypes); 
``` 
Методы getMethod() и getMethods() возвращают только открытые методы, для того чтобы получить все методы класса не зависимо от типа доступа, нужно воспользоватся методами getDeclaredMethod() и getDeclaredMethods(), которые работают точно также как и их аналоги (getMethod() и getMethods()). Интерфейс Java Reflection Api позволяет динамически вызвать метод, даже если во время компиляции имя этого метода неизвестно (Имена методов класса можно получить методом getMethods() или getDeclaredMethods()). В следующем примере рассмотрим вызов метода зная его имя. Например метод getCalculateRating:
```java 
Class c = obj.getClass(); 
Class[] paramTypes = new Class[] { String.class, int.class }; 
Method method = c.getMethod("getCalculateRating", paramTypes); 
Object[] args = new Object[] { new String("First Calculate"), new Integer(10) }; 
Double d = (Double) method.invoke(obj, args); 
``` 
В данном примере сначала получаем объект Method по имени метода getCalculateRating, затем вызываем метод invoke() объекта Method, и получаем результат работы метода. Метод invoke принимает два параметра, первый - это объект, класс которого объявляет или наследует данный метод, а второй - массив значений параметров, которые передаются вызываемому методу. Если метод имеет модификатор доступа private, тогда выше приведённый код нужно модифицировать таким образом, для объекта Method вместо метода getMethod() вызываем getDeclaredMethod(), затем для получения доступа вызываем setAccessible(true).
```java 
Method method = c.getDeclaredMethod("getCalculateRating", paramTypes); 
method.setAccessible(true);
``` 

#### Загрузка и динамическое создание экземпляра класса
```java 
Class c = Class.forName("Test"); 
Object obj = c.newInstance(); 
Test test = (Test) obj; 
``` 
С помощью методов Class.forName() и newInstance() объекта Class можно динамически загружать и создавать экземпляры класса в случае, когда имя класса неизвестно до момента выполнения программы. В приведенном коде мы загружаем класс с помощью метода Class.forName(), передавая имя этого класса. В результате возвращается объект типа Class. Затем мы вызываем метод newInstance() для объекта типа Class, чтобы создать экземпляры объекта исходного класса. Метод newInstance() возвращает объет обобщенного типа Object, поэтому в последней строке мы приводим возвращенный объект к тому типу, который нам нужен.

И напоследок, пример модификации private полей.
```java 
import java.lang.reflect.Field; 
 
class WithPrivateFinalField { 
    private int i = 1; 
    private final String s = "String S"; 
    private String s2 = "String S2"; 
 
    public String toString() { 
        return "i = " + i + ", " + s + ", " + s2; 
    } 
} 
 
public class ModifyngPrivateFields { 
 
    public static void main(String[] args) throws Exception { 
        WithPrivateFinalField pf = new WithPrivateFinalField(); 
         
        Field f = pf.getClass().getDeclaredField("i"); 
        f.setAccessible(true); 
        f.setInt(pf, 47); 
        System.out.println(pf); 
 
        f = pf.getClass().getDeclaredField("s"); 
        f.setAccessible(true); 
        f.set(pf, "MODIFY S"); 
        System.out.println(pf); 
 
 
        f = pf.getClass().getDeclaredField("s2"); 
        f.setAccessible(true); 
        f.set(pf, "MODIFY S2"); 
        System.out.println(pf); 
    } 
} 
``` 
Из приведённого кода видно что private поля можно изменять. Для этого требуется получить объект типа java.lang.reflect.Field с помощью метода getDeclaredField(), вызвать метод setAccessible(true) и с помощью метода set() устанавливаем значение поля. Учтите что поле final при выполнении данной процедуры не выдаёт предупреждений, а значение поля остаётся прежним, т.е. final поля остаются неизменные.

















# Загрузка классов в Java 
Одной из основных особенностей платформы Java является модель динамической загрузки классов, которая позволяет загружать исполняемый код в JRE не перезагружая основое приложение. Такая особенность широко используется в серверах приложений, получивших последнее время высокую популярность.

Любой класс (экземпляр класса java.lang.Class в среде и .class файл в файловой системе), используемый в среде исполнения был так или иначе загружен каким-либо загрузчиком в Java. Для того, чтобы получить загрузчик, которым был загружен класс А, необходимо воспользоваться методом A.class.getClassLoader().

Классы загружаются по мере надобности, за небольшим исключением. Некоторые базовые классы из rt.jar (java.lang.* в частности) загружаются при старте приложения. Классы расширений ($JAVA_HOME/lib/ext), пользовательские и большинство системных классов загружаются по мере их использования.

### Виды загрузчиков:

Различают 3-и вида загрузчиков в Java. Это — базовый загрузчик (bootstrap), системный загрузчик (System Classloader), загрузчик расширений (Extension Classloader).

* Bootstrap — реализован на уровне JVM и не имеет обратной связи со средой исполнения. Данным загрузчиком загружаются классы из директории $JAVA_HOME/lib. Т.е. всеми любимый rt.jar загружается именно базовым загрузчиком. Поэтому, попытка получения загрузчика у классов java.* всегда заканчиватся null'ом. Это объясняется тем, что все базовые классы загружены базовым загрузчиком, доступа к которому из управляемой среды нет.
Управлять загрузкой базовых классов можно с помощью ключа -Xbootclasspath, который позволяет переопределять наборы базовых классов.

* System Classloader — системный загрузчик, реализованный уже на уровне JRE. В Sun JRE — это класс sun.misc.Launcher$AppClassLoader. Этим загрузчиком загружаются классы, пути к которым указаны в переменной окружения CLASSPATH.
Управлять загрузкой системных классов можно с помощью ключа -classpath или системной опцией java.class.path.

* Extension Classloader — загрузчик расширений. Данный загрузчик загружает классы из директории $JAVA_HOME/lib/ext. В Sun JRE — это класс sun.misc.Launcher$ExtClassLoader.
Управлять загрузкой расширений можно с помощью системной опции java.ext.dirs.

Понятия:

* Current Classloader — это загрузчик класса, код которого в данный момент исполняется. Текущий загрузчик используется по умолчанию для загрузки классов в процессе исполнения. В часности, при использовании метода Class.forName("")/ClassLoader.loadClass("") или при любой декларации класса, ранее не загруженного.
* Context Classloader — загрузчик контекста текущего потока. Получить и установить данный загрузчик можно с помощью методов Thread.getContextClassLoader()/Thread.setContextClassLoader(). Загрузчик контекста устанавливается автоматически для каждого нового потока. При этом, используется загрузчик родительского потока.

***Inside***

Запустим простейшее приложениие с ключем -verbose:class.
```java 
public class A { }

public class B extends A { }

public class C extends B { }

public class Main {
  
  public static void main(String args[]) {
    C c = new C();
    B b = new B();
    A a = new A();
  }
}
```
Вывод показывает, что классы были загружены не в том порядке в котором были использованы. Это обусловлено наследованием.
```text 
[Loaded Main from file:/C:/devel/CL/bin/]
[Loaded A from file:/C:/devel/CL/bin/]
[Loaded B from file:/C:/devel/CL/bin/]
[Loaded C from file:/C:/devel/CL/bin/] 
```

Зачастую, архитектура сложных систем подразумевает использование механизма динамической загрузки кода. Это бывает необходимо, когда заранее не известно какой именно код будет исполняться в рантайме. Например, всем известная игра для программистов Robocode использует собственный загрузчик классов, для загрузки пользовательских танков в игру. Можно рассматривать отдельный танк как модуль, разрабатываемый изолированно от приложения по заданному интерфейсу. Похожая ситуация рассматривается в статье, только на максимально упрощенном уровне.

Кроме того, можно привести еще несколько очевидных примеров использования механизма динамической загрузки кода. Допустим байт-код классов хранится в БД. Очевидно, что для загрузки таких классов нужен специальный загрузчик, в обязанности которого будет входить еще и выборка кода классов из БД. 

Возможно, классы требуется загружать по сети/через интернет. Для таких целей нужен загрузчик, способный получать байт-код по одному из сетевых протоколов. Можно также выделить, существующий в Java Class Library URLClassLoader, который способен загружать классы по указанному пути в URL.

Реализуемое в рамках статьи приложение будет представлять собой каркас движка для динамической загрузки кода в JRE и его исполнения. Каждый модуль будет представлять собой один Java класс, реализующий интерфейс Module. Общий для всех модулей интерфейс необходим для их инвокации. Здесь, важно понимать, что существует еще один способ исполнения динамического кода — Java Reflection API. Однако, для большей наглядности и простоты будет использоваться модель с общим интерфейсом.

При реализации пользовательских загрузчиков важно помнить следующее:
1) любой загрузчик должен явно или неявно расширять класс java.lang.ClassLoader;
2) любой загрузчик должен поддерживать модель делегирования загрузки, образуя иерархию;
3) в классе java.lang.ClassLoader уже реализован метод непосредственной загрузки — defineClass(...), который байт-код преобразует в java.lang.Class, осуществляя его валидацию;
4) механизм рекурентного поиска также реализован в классе java.lang.ClassLoader и заботиться об это не нужно;
5) для корректной реализации загрузчика достаточно лишь переопределить метод findClass() класса java.lang.ClassLoader.

Рассмотрим детально поведение загрузчика классов при вызове метода loadClass() для объяснения последнего пункта вышеуказанного списка.

Реализация по-умолчанию подразумевает следующую последовательность действий:
1) вызов findLoadedClass() для поиска загружаемого класса в кеше;
2) если класса в кеше не оказалось, происходит вызов getParent().loadClass() для делегирования права загрузки родительскому загрузчику;
3) если иерархия родительских загрузчиков не смогла загрузить класс, происходит вызов findClass() для непосредственной загрузки класса.

Поэтому для правильной реализации загрузчиков рекомендуется придерживаться указанного сценария — переопределения метода findClass().

Определим интерфейс модулей. Пусть модуль сначала загружается (load), потом исполняется (run), возвращая результат и затем уже выгружается (unload). Данный код представляет собой API для разработки модулей. Его можно скомпилировать отдельно и упаковать в *.jar для поставки отдельно от основного приложения.
```java 
public interface Module {
  
  public static final int EXIT_SUCCESS = 0;
  public static final int EXIT_FAILURE = 1;
  
  public void load();
  public int run();
  public void unload();

}
```
Рассмотрим реализацию загрузчика модулей. Данный загрузчик загружает код классов из определенной директории, путь к которой указан в переменной pathtobin.

```java 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ModuleLoader extends ClassLoader {
  
  /**
   * Путь до директории с модулями.
   */
  private String pathtobin;
  
  public ModuleLoader(String pathtobin, ClassLoader parent) {
    super(parent);    
    this.pathtobin = pathtobin;    
  }

  @Override
  public Class<?> findClass(String className) throws ClassNotFoundException {
    try {
      /**
       * Получем байт-код из файла и загружаем класс в рантайм 
       */
      byte b[] = fetchClassFromFS(pathtobin + className + ".class");
      return defineClass(className, b, 0, b.length);
    } catch (FileNotFoundException ex) {
      return super.findClass(className);
    } catch (IOException ex) {
      return super.findClass(className);
    }
    
  }
  
  /**
   * Взято из www.java-tips.org/java-se-tips/java.io/reading-a-file-into-a-byte-array.html
   */
  private byte[] fetchClassFromFS(String path) throws FileNotFoundException, IOException {
    InputStream is = new FileInputStream(new File(path));
    
    // Get the size of the file
    long length = new File(path).length();
  
    if (length > Integer.MAX_VALUE) {
      // File is too large
    }
  
    // Create the byte array to hold the data
    byte[] bytes = new byte[(int)length];
  
    // Read in the bytes
    int offset = 0;
    int numRead = 0;
    while (offset < bytes.length
        && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
      offset += numRead;
    }
  
    // Ensure all the bytes have been read in
    if (offset < bytes.length) {
      throw new IOException("Could not completely read file "+path);
    }
  
    // Close the input stream and return bytes
    is.close();
    return bytes;

  }
}
```
Теперь рассмотрим реализацию движка загрузки модулей. Директория с модулями (файлами .class) указывается в качестве параметра приложению.

```java 
import java.io.File;

public class ModuleEngine {
  
  public static void main(String args[]) {
    String modulePath = args[0];
    /**
     * Создаем загрузчик модулей.
     */
    ModuleLoader loader = new ModuleLoader(modulePath, ClassLoader.getSystemClassLoader());
    
    /**
     * Получаем список доступных модулей.
     */
    File dir = new File(modulePath);
    String[] modules = dir.list();
    
    /**
     * Загружаем и исполняем каждый модуль. 
     */
    for (String module: modules) {
      try {
        String moduleName = module.split(".class")[0];
        Class clazz = loader.loadClass(moduleName);
        Module execute = (Module) clazz.newInstance(); 
        
        execute.load();
        execute.run();
        execute.unload();
        
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }
}
```
Реализуем простейший модуль, который просто печатает на стандартный вывод информацию о стадиях своего исполнения. Это можно сделать в отдельном приложении добавив к CLASSPATH путь до скомпилированного .jar файла c классом Module (API).

```java 
public class ModulePrinter implements Module {

  @Override
  public void load() {
    System.out.println("Module " + this.getClass() + " loading ...");
  }

  @Override
  public int run() {
    System.out.println("Module " + this.getClass() + " running ...");
    return Module.EXIT_SUCCESS;
  }

  @Override
  public void unload() {
    System.out.println("Module " + this.getClass() + " inloading ...");    
  }
}

```

Скомпилировав данный код, результат в виде одного class файла можно скопировать в отдельную директорию, путь к которой необходимо указать в качестве параметра основного приложения.

Динамическая загрузка кода, отличный и легальный способ предать обязанности по расширению системы пользователю ;)







