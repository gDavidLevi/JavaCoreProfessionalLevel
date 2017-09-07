# Логирование на уровне библиотеки org.apache.log4j

Данный фреймворк на текущий момент имеет уже вторую версию, которая увы не совместима с первой. 
Поскольку первая версия log4j существует достаточно давно и, в виду ее большой популярности, существует не мало статей на просторах интернета, сегодня мы рассмотрим вторую. 
Для использования log4j2 вам необходимо подключить библиотеки log4j-api-2.x и log4j-core-2.x. Log4j имеет несколько отличное от JUL именование уровней логгирования: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, а так же ALL и OFF включающий и отключающий все уровни соответственно.

Логгер создается вызовом статического метода класса org.apache.logging.log4j.Logger:
```java 
Logger log = LogManager.getLogger(LoggingLog4j.class);
// или
Logger log = LogManager.getLogger(“name”);
```
Логгер умеет принимать помимо привычных нам String, Object и Throwable еще два новых типа — MapMessage и Marker:
```java 
/* Карта сообщений (напечатается как msg1="Сообщение 1” msg2="Сообщение 2”) */ 
MapMessage mapMessage = new MapMessage();  
mapMessage.put("msg1", "Сообщение 1");
mapMessage.put("msg2", "Сообщение 2");

/* Маркер, объект по которому можно фильтровать сообщения */ 
Marker marker = MarkerManager.getMarker("fileonly");

/* Строковое сообщение */ 
String stringMessage = "Сообщение";

/* Строковое сообщение с параметрами */ 
String stringMessageFormat = "Сообщение {}, от {}";

/* Исключение */ 
Throwable throwable = new Throwable();

/* Объект */ 
Object object = new Object();
```
В классическом для логгеров стиле методы делятся на два типа: совпадающие с названием уровня логгирования и методы log, принимающие уровень логгирования в качестве параметра. Первые имеют вид:
```java 
log.info(mapMessage);
log.info(object);
log.info(stringMessage);
log.info(marker, mapMessage);
log.info(marker, object);
log.info(marker, stringMessage);
log.info(object, throwable);
log.info(stringMessage, throwable);
log.info(stringMessageFormat, args);
log.info(marker, mapMessage, throwable);
log.info(marker, object, throwable);
log.info(marker, stringMessageFormat, args);
log.info(marker, stringMessage, throwable);
log.throwing(throwable);
```
Методы log в log4j2 выглядят так:
```java 
log.log(Level.INFO, mapMessage);
log.log(Level.INFO, object);
log.log(Level.INFO, stringMessage);
log.log(Level.INFO, marker, mapMessage);
log.log(Level.INFO, marker, object);
log.log(Level.INFO, marker, stringMessage);
log.log(Level.INFO, object, throwable);
log.log(Level.INFO, stringMessageFormat, args);
log.log(Level.INFO, stringMessage, throwable);
log.log(Level.INFO, marker, mapMessage, throwable);
log.log(Level.INFO, marker, object, throwable);
log.log(Level.INFO, marker, stringMessageFormat, args);
log.log(Level.INFO, marker, stringMessage, throwable);
log.throwing(Level.INFO, throwable);
```
Если не определить конфигурацию, то при запуске log4j2 выдаст гневное сообщение, о том, что конфигурация не задана и будет печатать ваши сообщения на консоль уровнем не ниже ERROR. Конфигурация log4j2 задается несколькими вариантами: xml, json, yaml. Стоит отметить, что со второй версии нет поддержки конфигурации из property файла. Файл с конфигурацией автоматически ищется classpath, должен иметь название log4j2 и располагаться в пакете по умолчанию. 

Конфигурация log4j2 состоит из описания логгеров, аппендеров и фильтров. Для более детального изучения обратитесь к документации, сейчас же лишь отметим пару ключевых моментов. Во-первых, есть различные вкусности в виде фильтров, в том числе и по маркерам:
* BurstFilter
* CompositeFilter
* DynamicThresholdFilter
* MapFilter
* MarkerFilter
* RegexFilter
* StructuredDataFilter
* ThreadContextMapFilter
* ThresholdFilter
* TimeFilter

Во-вторых, имеется широкий круг классов аппендеров, в том числе асинхронные аппендеры и аппендеры оборачивающие группу других аппендеров:
* AsyncAppender
* ConsoleAppender
* FailoverAppender
* FileAppender
* FlumeAppender
* JDBCAppender
* JMSAppender
* JPAAppender
* MemoryMappedFileAppender
* NoSQLAppender
* OutputStreamAppender
* RandomAccessFileAppender
* RewriteAppender
* RollingFileAppender
* RollingRandomAccessFileAppender
* RoutingAppender
* SMTPAppender
* SocketAppender
* SyslogAppender

Стоит также заметить, что log4j может создавать множество различающихся аппендеров одного и того же класса, например несколько файловых аппендеров, которые пишут в разные файлы.

Рассмотрим пример конфигурации, в которой объявлены два логгера (корневой и для нашего класса), первый из которых пишет в файл log.log, а второй пишет в log2.log с использованием фильтрации по маркеру:
```xml 
<?xml version="1.0" encoding="UTF-8"?>
<Configuration>
  <!-- Секция аппендеров -->
  <Appenders>
    <!-- Файловый аппендер -->
    <File name="file" fileName="log.log">
      <PatternLayout>
        <Pattern>%d %p %c{1.} [%t] %m %ex%n</Pattern>
      </PatternLayout>
    </File>
    <!-- Файловый аппендер -->
    <File name="file2" fileName="log2.log">
      <!-- Фильтр по маркеру -->
      <MarkerFilter marker="fileonly" onMatch="DENY" onMismatch="ACCEPT"/>
      <PatternLayout>
        <Pattern>%d %p %c{1.} [%t] %m %ex%n</Pattern>
      </PatternLayout>
    </File>
  </Appenders>
  <!-- Секция логгеров -->
  <Loggers>
    <!-- Корневой логгер -->
    <Root level="trace">
      <AppenderRef ref="file" level="DEBUG"/>
    </Root>
    <!-- Логгер нашего класса -->
    <Logger name="logging.log4j.LoggingLog4j" level="info" additivity="false">
        <AppenderRef ref="file2" level="INFO"/>
    </Logger>
  </Loggers>
</Configuration> 
```
 
 