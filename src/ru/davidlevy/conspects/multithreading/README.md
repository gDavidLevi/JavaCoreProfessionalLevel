# Многопоточность

Содержание
* Виды потоков
* Создание и запуск нитей
    * main-нить
    * новый класс, наследник от Thread
    * через реализацию интерфейса Runnable
    * через анонимный класс
    * через лямбду
    * используя ExecutorService
    * используя ScheduledExecutorService
    * используя ThreadPoolExecutor
    * используя ScheduledThreadPoolExecutor 
    * используя ForkJoinPool
* Свойства нитей
    * приоритет выполнения
    * получение состояния нити исполнения
* Варианты запуска нитей
    * метод run() - используется для выполнения в main-нити
    * метод start() - более корректный способ запуска нитей
    * используя Executors
* Ожидание завершения
    * main-нить может завершается раньше, чем другие запущенные из main
    * если надо дождаться завершения нити t1, то применяется метод join()
    * ожидание завершения ExecutorService
* Принудительное завершение нитей
    * автоматическое завершение нити-демона после завершения main-нити
    * отправка сигнала interrupt() 
    * выход из цикла нити после обработки булевого значения
    * отправка сигнала shutdownNow() в ExecutorService
* ExecutorService
    * методы execute(), submit(), shutdown(), shutdownNow() и invokeAll()
    * фабрики newSingleThreadExecutor, newFixedThreadPool, newCachedThreadPool и newWorkStealingPool
    * метод invokeAll() 
    * метод invokeAny()
* ScheduledExecutorService - исполнители с планировщиком
* ThreadPoolExecutor - запуск асинхронных задач в пуле потоков
* ScheduledThreadPoolExecutor - запуск асинхронных задач в пуле потоков по расписанию
* ForkJoinPool - дробление задачи на нити и распределение их по ядрам процессора 
* Получение данных из Executor (Callable, FutureTask, Future)
* Обработка исключений
* Синхронизация потоков
    * механизм взаимодействия потоков исполнения; wait(), notify() и notifyAll() 
    * метод join() 
    * synchronized 
    * объект класса StringBuffer
    * по переменной 
    * синхронизаторы из библиотеки java.util.concurrent 
    * синхронизированные коллекции 




    


## Виды потоков

В Java существует 2 вида потока в рамках процесса (на уровне ОС каждая программа - это процесс): 
- stream (поток) - чтение и запись, см. Stream API
- thread (нить) - параллельный поток; об этом в конспекте


## Создание и запуск нитей

Жизни потоков:
- обычных: создается поток, выполняшка (new Runnable) отрабатывает и нить умирает; повторно запустить её нельзя.
- из пула (ExecutorService): пул для нитей создается и не умирает, сервис ожидает какую выполняшку, которую можно запихнуть на выполнение.

#### main-нить
```java 
public static void main(String[] args) {
   System.out.println(Thread.currentThread().getName());  // main
}
``` 
  
#### новый класс, наследник от Thread
```java 
public class MainClass {
   static class MyThread extends Thread {
       @Override
       public void run() {
           for (int i = 0; i < 10; i++) {
               try {
                   Thread.sleep(100);
                   System.out.println("new thread: " + i);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       }
   }
}

public static void main(String[] args) {
   MyThread t1 = new MyThread();
   t1.start();  
   // или
   new MyThread().start(); 
}
``` 
#### через реализацию интерфейса Runnable
```java 
public class MainClass {
   static class MyRunnable implements Runnable{
       @Override
       public void run() {
           for (int i = 0; i < 10; i++) {
               try {
                   Thread.sleep(100);
                   System.out.println(Thread.currentThread().getName() + " - " + i);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       }
   }
   
   public static void main(String[] args) {
      MyRunnable obj = new MyRunnable();
      Thread t1 = new Thread(obj);
      t1.start();    
   }
}
``` 
#### через анонимный класс
```java 
new Thread(new Runnable() {
   @Override
   public void run() {
       for (int i = 0; i < 10; i++) {
           try {
               Thread.sleep(100);
               System.out.println(Thread.currentThread().getName() + " - " + i);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
   }
}).start();
``` 
#### через лямбду
```java 
new Thread(() -> System.out.println("")).start(); 
``` 
```java  
new Thread(() -> {
       for (int i = 0; i < 10; i++) {
           try {
               Thread.sleep(100);
               System.out.println(Thread.currentThread().getName() + " - " + i);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
}).start();
``` 
#### используя Executors, ScheduledExecutorService, ThreadPoolExecutor, ScheduledThreadPoolExecutor и ForkJoinPool
```text 
см. далее
```







## Свойства нитей
```java 
Thread thread = new Thread(new Runnable() {
           @Override
           public void run() {
             /* Код */
           }
       }, "Имя_нити");

       thread.start(); // запуск
       thread.setName("Моя нить"); // имя
       thread.setDaemon(true); // сделать поток-нить демоном
       thread.getName(); // получить имя
       thread.isAlive(); // запущен ли поток-нить?
       thread.isInterrupted(); // завершен ли поток-нить?
       thread.isDaemon(); // это демон?
       thread.setPriority(5); // установить приоритет запуска; только для Windows
       thread.getPriority(); // получить текущий приоритет
       System.out.println(Thread.currentThread().getName()); // получить имя текущей нити, в данном случае это main
       ...и другие.
``` 
#### Приоритет выполнения 
До запуска нити потока можно указать ему приоритет запуска/выполнения от 1 до 10, где 5 - значение по умолчанию.

По умолчанию политика планировщика потоков Linux (SCHED_OTHER) не поддерживает приоритеты. Или, точнее, он поддерживает настройку приоритета с одним значением: 0. Другие так называемые политики реального времени SCHED_FIFO и SCHED_RR поддерживают более высокие приоритеты, но доступны только для процессов с привилегиями суперпользователя.

Установка приоритетов актуально только для Windows:
```java 
public static void main(String[] args) {
   Thread t1 = new Thread(() -> {
       for (int i = 0; i < 10; i++) {
           try {
               Thread.sleep(100);
               System.out.println(Thread.currentThread().getName() + " - " + i);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
   });

   /* Установка приоритета */ 
   t1.setPriority(5); 
   
   t1.start();
}
```
#### получение состояния нити исполнения
Варианты состояний:
- BLOCКED – нить приостановила выполнение, поскольку ожидает получения блокировки;
- NEW – нить ещё не начала выполнение;
- RUNNAВLE – нить в настоящее время выполняется или начнёт выполняться, когда получит доступ к ЦП;
- TERМINATED – нить завершила выполнение;
- TIМED WAITING – нить приостановила выполнение на определенный промежуток времени, например, после вызова метода sleep(). Нить переходит в это состояние и при вызове метода wait() или join();
- WAITING – нить приостановила выполнение, поскольку она ожидает некоторого действия, например, вызова версии метода wait() или join() без заданного времени ожидания.
```java 
public static void main(String[] args) throws InterruptedException {
    System.out.println(Thread.currentThread().getState());
}
```
```text 
_______________________________
/opt/jdk/bin/java...
RUNNABLE

Process finished with exit code 0
```




















## Варианты запуска нитей
При запуске нескольких нитей-потоков, мы точно не знаем какая из нитей запустится первым. 

#### метод run() - используется для выполнения в main-нити
```java 
public static void main(String[] args) {
   Thread t1 = new Thread(() -> {
       for (int i = 0; i < 10; i++) {
           try {
               Thread.sleep(100);
               System.out.println(Thread.currentThread().getName() + " - " + i);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
   });

   Thread t2 = new Thread(() -> {
       for (int i = 10; i < 20; i++) {
           try {
               Thread.sleep(180);
               System.out.println(Thread.currentThread().getName() + " - " + i);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
   });

   t1.run();
   t2.run();
```
```text 
_______________________________
/opt/jdk/bin/java...
main - 0
main - 1
main - 2
main - 3
main - 4
main - 5
main - 6
main - 7
main - 8
main - 9
main - 10
main - 11
main - 12
main - 13
main - 14
main - 15
main - 16
main - 17
main - 18
main - 19

Process finished with exit code 0
```

#### метод start() - более корректный способ запуска нитей
```java 
public static void main(String[] args) {
   Thread t1 = new Thread(() -> {
       for (int i = 0; i < 10; i++) {
           try {
               Thread.sleep(100);
               System.out.println(Thread.currentThread().getName() + " - " + i);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
   });

   Thread t2 = new Thread(() -> {
       for (int i = 10; i < 20; i++) {
           try {
               Thread.sleep(180);
               System.out.println(Thread.currentThread().getName() + " - " + i);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
   });

   t1.start();
   t2.start();
}
``` 
```text 
_______________________________
/opt/jdk/bin/java...
Thread-0 - 0
Thread-1 - 10
Thread-0 - 1
Thread-0 - 2
Thread-1 - 11
Thread-0 - 3
Thread-0 - 4
Thread-1 - 12
Thread-0 - 5
Thread-0 - 6
Thread-1 - 13
Thread-0 - 7
Thread-1 - 14
Thread-0 - 8
Thread-0 - 9
Thread-1 - 15
Thread-1 - 16
Thread-1 - 17
Thread-1 - 18
Thread-1 - 19

Process finished with exit code 0
```

#### используя ExecutorService
```text 
    см. далее
```



























## Ожидание завершения
#### main-нить может завершается раньше, чем другие запущенные из main
```java 
public class MainClass {
   public static void main(String[] args) {
       Thread t1 = new Thread(() -> {
           try {
               Thread.sleep(5000);
               System.out.println("Нить t1 завершилась.");
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       });
       t1.start();

       System.out.println("main-нить завершилась.");
   }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
main-нить завершился.
Нить t1 завершилась.

Process finished with exit code 0
```

#### если надо дождаться завершения нити t1, то применяется метод join()
```java 
public class MainClass {
   public static void main(String[] args) throws InterruptedException {
       Thread t1 = new Thread(() -> {
           try {
               Thread.sleep(5000);
               System.out.println("Поток t1 завершился.");
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       });
       t1.start();
       
       /* main-поток будет ожидать завершения потока t1 */ 
       t1.join();
       
       System.out.println("Поток main завершился.");
   }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
Поток t1 завершился.
Поток main завершился.

Process finished with exit code 0
```

#### ожидание завершения ExecutorService
Флаг isTerminated() устанавливается в true, если потоки в пуле завершились.
Метод awaitTermination(единиц, тип_единиц) определяет время ожидания завершения в течении числа секунд, минут, часов...
```java 
public static void main(String[] args) {
    ExecutorService pool = Executors.newFixedThreadPool(2);
    
    for (Car car : cars) 
        pool.execute(new Thread(car));
    pool.shutdown();
    
    /* Ожидание завершения нитей. Неудобный способ! */ 
    while (true) {
        if (pool.isTerminated()) {
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка завершилась!!!");
            break;
        }
    }
    
    /* Ожидание 10 минут. Некоторое подобие JOIN */ 
    pool.awaitTermination(10, TimeUnit.MINUTES);
} 
```




































## Принудительное завершение нитей
допустим существует нить-поток-таймер t, но завершить его можно только вручную:
```java 
public static void main(String[] args) throws InterruptedException {
   Thread t = new Thread(() -> {
       int time = 0;
       while (true) {
           try {
               Thread.sleep(1000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           time++;
           System.out.println(time);
       }
   });
   t.start();
   Thread.sleep(4000);
   System.out.println("main end.");
}
```
```text 
_______________________________
/opt/jdk/bin/java...
1
2
3
main end.
4
5
6
7
8
Process finished with exit code 130 (interrupted by signal 2: SIGINT)
```
В итоге он завершится с ошибкой 130.

#### автоматическое завершение нити-демона после завершения main-нити
```java 
public static void main(String[] args) throws InterruptedException  {
   Thread t = new Thread(() -> {
       int time = 0;
       while (true) {
           try {
               Thread.sleep(1000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           time++;
           System.out.println(time);
       }
   });

   /* Данная нить завершится после завершения main-нити */
   t.setDaemon(true);

   t.start();
   Thread.sleep(4000);
   System.out.println("main end.");
}
```
```text 
_______________________________
/opt/jdk/bin/java...
1
2
3
main end.

Process finished with exit code 0
```
После этого нить (Thread t) завершается после завершения главного main-нити с кодом ошибки 0. Что нам и требовалось.

#### отправка сигнала interrupt() 
```java 
    public static void main(String[] args) throws InterruptedException {
        System.out.println("main start...");
        Thread t = new Thread(() -> {
            int time = 1;
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println(" Thread t:: " + time++ + " sec.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();

        Thread.sleep(5000);

        /* Отправить сигнал в нить t, чтобы он завершиться через 5 секунды */
        t.interrupt();

        System.out.println("main end.");
    }
```
```text 
_______________________________
/opt/jdk/bin/java...
main start...
 Thread t:: 1 sec.
 Thread t:: 2 sec.
 Thread t:: 3 sec.
 Thread t:: 4 sec.
 Thread t:: 5 sec.
main end.

Process finished with exit code 0
```

аналогично:
```java 
public class Multithreading {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    /* Нить попытались закрыть с помощью thread.interrupt() */ 
                    if (Thread.currentThread().isInterrupted()) { 
                        break;
                    }
                }
            }
        });
        thread.start();
        System.out.printf("Уже завершился поток %s%n", Thread.currentThread().getName());
        Thread.sleep(2000);
        System.out.println("Прошло 2 секунды.");

        thread.interrupt();
        if (thread.isInterrupted())
            System.out.println("Поток thread завершился.");
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
Уже завершился поток main
Прошло 2 секунды.
Поток thread завершился.

Process finished with exit code 0
```
#### выход из цикла нити после обработки булевого значения
```java 
public class Multithreading {
    public static void main(String[] args) {
        MyRunnable runnable = new MyRunnable();
        Thread thread = new Thread(runnable);
        thread.start();

        /* Остановить нить! */
        runnable.stop();
    }
}

class MyRunnable implements Runnable {
    private volatile boolean running = true;

    void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        while (this.running) {
            try {
                /* 15 секунд */
                Thread.sleep((long) 15000);
            } catch (InterruptedException e) {
                this.running = false;
            }
        }
    }
} 
```

#### отправка сигнала shutdownNow() в ExecutorService
shutdownNow() отправляет потокам сигнал interrupt(), если внутри потока есть код Thread.currentThread().isInterrupted():
```java 
// Цикл для new Runable() или new Callable
while (true) { 
    if (Thread.currentThread().isInterrupted()) { 
        // Данный поток завершен!
        break;
    }
}
```

















# Executors

## ExecutorService
Более эффективный способ создавать нити-потоков.
ExecutorService создает пул и исполняет асинхронный код в одной или нескольких нитях. 

### методы execute(), submit(), shutdown() и shutdownNow()
- execute(new Runnable) выполняет выполняшку в пуле.
- submit(new Callable) подписывается на звонилку (new Callable).
Метод submit также возвращает объект Future, который содержит информацию о статусе исполнения переданного  Callable (который может возвращать значение).
Вызов метода get на объекте Future возвратит значение, который возвращает Callable.
- submit(new Runnable) подписывается на выполняшку (new Runnable).
Метод submit также возвращает объект Future, который содержит информацию о статусе исполнения переданного Runnable.
Из него можно узнать выполнился ли переданный код успешно, или он еще выполняется. 
Вызов метода get на объекте Future возвратит значение возвращает null. 
- submit(new Runnable, T result) может возвращать значение Future\<T>.
- shutdown() запрещает добавлять выполняшки в пул.
- shutdownNow() отправляет потокам сигнал interrupt(), если внутри потока есть код Thread.currentThread().isInterrupted().
- invokeAll(Collection<? extends Callable<T>>) - гарантированное ожидание завершение всех нитей в пуле
- invokeAny(Collection<? extends Callable<T>>) - гарантированное ожидание завершение одной нити из пула

### фабрики newSingleThreadExecutor, newFixedThreadPool, newCachedThreadPool и newWorkStealingPool
Создание инстанса ExecutorService'а делается либо вручную через конкретные имплементации (ScheduledThreadPoolExecutor или ThreadPoolExecutor), но проще будет использовать фабрики класса Executors:
- newSingleThreadExecutor - только Одна нить для одной выполняшки. 
- newFixedThreadPool - пулл с фиксированным числом нитей
- newCachedThreadPool - кэширующий пул нитей, который создает нити по мере необходимости 
- newWorkStealingPool - создает ForkJoinPool с определенным параллелизмом (parallelism size), по умолчанию равным количеству ядер машины, см. метод invokeAll и invokeAny.

Какую выбрать фабрику:
- newCachedThreadPool используется если очень большой сервис, которой очень сильно нагружен разными потоками
- определение нагрузки. Первоначально используем newCachedThreadPool, определяем со временем насколько он сильно загружен, затем фиксируем пут используя newFixedThreadPool.
- newSingleThreadExecutor создан, чтобы запускать редкие, но долго выполняющиеся выполняшки.
- newWorkStealingPool используется при вызове метода invokeAll и invokeAny.

***newSingleThreadExecutor***
```java 
ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
Callable callable = new Callable() {
   @Override
   public Object call() throws Exception {
       try {
           TimeUnit.SECONDS.sleep(1);
           return 123;
       } catch (InterruptedException e) {
           throw new IllegalStateException("task interrupted", e);
       }
   }
};
Future<Integer> future = singleThreadExecutor.submit(callable);
System.out.println("Результат: " + future.get());
singleThreadExecutor.shutdown();
```

***newFixedThreadPool***
```java 
ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
fixedThreadPool.submit(new Runnable() {
   @Override
   public void run() {
       // code
   }
});
```

***newCachedThreadPool***
```java 
ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
cachedThreadPool.submit(new Runnable() {
   @Override
   public void run() {
       // code...
   }
});
cachedThreadPool.shutdown();
```

***newWorkStealingPool***
Метод newWorkStealingPool() появился в Java 8 и ведет себя не так, как другие: вместо использования фиксированного количества нитей он создает ForkJoinPool с определенным параллелизмом (parallelism size), по умолчанию равным количеству ядер машины.
См. метод invokeAll и invokeAny.


#### метод invokeAll(Collection<? extends Callable<T>>) - гарантированное ожидание завершение всех нитей
Используется для того, чтобы отдать на выполнение списка задач не заморачиваясь на проверки и ожидание завершения нитей.
```java 
public static void main(String... args) throws InterruptedException, ExecutionException {
    /* Список задач */
    List<Callable<Object>> array = new ArrayList<>();

    /* задача 1 */
    array.add(() -> {
        System.out.println("A");
        Thread.sleep(50);
        return "a";
    });

    /* задача 2 */
    array.add(() -> {
        System.out.println("B");
        Thread.sleep(50);
        return "b";
    });

    /* задача 3 */
    array.add(new Callable<Object>() {
        @Override
        public Object call() throws Exception {
            System.out.println("C");
            Thread.sleep(50);
            return "c";
        }
    });

    ExecutorService service = Executors.newFixedThreadPool(array.size());

    /* Гарантированное ожидание завершение нитей */
    service.invokeAll(array);

    /* Получение списка результатов всех нитей */
    List<Future<Object>> resultats = service.invokeAll(array); // [a, b, c]

    /* Переменные с результатами из каждой нити */
    String r1, r2, r3, r4;

    r1 = (String) resultats.get(0).get();
    r2 = (String) resultats.get(1).get();
    r3 = (String) resultats.get(3).get();
    r4 = (String) resultats.get(4).get();

    service.shutdown();
}
```

В следующем примере мы использовали функциональные нити для обработки задач, возвращенных методом invokeAll. Мы прошлись по всем задачам и вывели их результат на консоль:
```java 
public static void main(String... args) throws InterruptedException, ExecutionException {
    /* Список задач */
    List<Callable<String>> array = Arrays.asList(
            () -> "a",
            () -> "b",
            new Callable<String>() {
                @Override
                public String call() throws Exception {
                    System.out.println("C");
                    Thread.sleep(50);
                    return "c";
                }
            });

    ExecutorService service = Executors.newWorkStealingPool();
    
    /* Гарантированное ожидание завершение нитей */
    service.invokeAll(array)
            .stream()
            .map(future -> {
                try {
                    return future.get();
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            })
            .forEach(System.out::println);

    service.shutdown();
}
```

#### метод invokeAny(Collection<? extends Callable<T>>) - гарантированное ожидание завершение именно одной нити из пула
Вместо возврата Future он блокирует нить до того, как завершится хоть одна задача, и возвращает ее результат.

Используем этот метод, чтобы создать несколько задач с разными строками и задержками от одной до трех секунд. 
Отправка этих задач исполнителю через метод invokeAny() вернет результат задачи с наименьшей задержкой. 
В данном случае это «1 sec»:
```java 
public class MainClass { 
    private static Callable callable(String result, long sleepSeconds) {
        return () -> {
            TimeUnit.SECONDS.sleep(sleepSeconds);
            return result;
        };
    }

    public static void main(String... args) throws InterruptedException, ExecutionException {
        List<Callable<String>> array = Arrays.asList(
                callable("2 sec", 2),

                /* Мы получим именно "1 sec" потому, что эта нить завершится первой */
                callable("1 sec", 1),

                callable("3 sec", 3));

        ExecutorService service = Executors.newWorkStealingPool();

        String result = service.invokeAny(array);
        System.out.println(result);  // 1 sec

        service.shutdown();
    }
}
```


 
 
 
 



## ScheduledExecutorService - исполнители с планировщиком
Иногда требуется выполнение кода асихронно и периодически или требуется выполнить код через некоторое время, тогда на помощь приходит ScheduledExecutorService. Он позволяет поставить код выполняться в одном или нескольких потоках и сконфигурировать интервал или время, на которое выполненение будет отложено. Интервалом может быть время между двумя последовательными запусками или время между окончанием одного выполнения и началом другого. Методы ScheduledExecutorService возвращают ScheduledFuture, который также содержит значение отсрочки для выполнения ScheduledFuture.

Если требуется отложить выполнение на 5 секунд, потребуется следующий код:
```java 
ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
service.schedule(new Runnable() { ... }, 5, TimeUnit.SECONDS);
```
Если требуется назначить выполнение каждую секунду:
```java 
ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
service.scheduleAtFixedRate(new Runnable() { ... }, 0, 1, TimeUnit.SECONDS);
```
Если требуется назначить выполнение кода с промежутком 1 секунда между выполнениями:
```java 
ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
service.scheduleWithFixedDelay(new Runnable() { ... }, 0, 1, TimeUnit.SECONDS);
```

Как заставить исполнитель выполнить задачу через 3 секунды:
```java 
public static void main(String[] args) throws Exception {
   ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

   Runnable task = () -> System.out.println("Scheduling: " + System.nanoTime());
   ScheduledFuture<?> future = executor.schedule(task, 3, TimeUnit.SECONDS);

   TimeUnit.MILLISECONDS.sleep(1337);
  
   long remainingDelay = future.getDelay(TimeUnit.MILLISECONDS);
   System.out.printf("Remaining Delay: %sms", remainingDelay);
}
```
Когда мы передаем задачу планировщику, он возвращает особый тип Future — ScheduledFuture, который предоставляет метод getDelay() для получения оставшегося до запуска времени.

У исполнителя с планировщиком есть два метода для установки задач: scheduleAtFixedRate() и scheduleWithFixedDelay(). Первый устанавливает задачи с определенным интервалом, например, в одну секунду:
```java 
public static void main(String[] args) throws Exception {
   ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

   Runnable task = () -> System.out.println("Scheduling: " + System.nanoTime());

   int initialDelay = 0;
   int period = 1;
   executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
}
```
Кроме того, он принимает начальную задержку, которая определяет время до первого запуска.

Обратите внимание, что метод scheduleAtFixedRate() не берет в расчет время выполнения задачи. Так, если вы поставите задачу, которая выполняется две секунды, с интервалом в одну, пул потоков рано или поздно переполнится.

В этом случае необходимо использовать метод scheduleWithFixedDelay(). Он работает примерно так же, как и предыдущий, но указанный интервал будет отсчитываться от времени завершения предыдущей задачи.
```java 
public static void main(String[] args) throws Exception {
   ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
   Runnable task = () -> {
       try {
           TimeUnit.SECONDS.sleep(2);
           System.out.println("Scheduling: " + System.nanoTime());
       }
       catch (InterruptedException e) {
           System.err.println("task interrupted");
       }
   };
   executor.scheduleWithFixedDelay(task, 0, 1, TimeUnit.SECONDS);
   //executor.shutdown();
}
```
В этом примере мы ставим задачу с задержкой в одну секунду между окончанием выполнения задачи и началом следующей. Начальной задержки нет, и каждая задача выполняется две секунды. Так, задачи будут запускаться на 0, 3, 6, 9 и т. д. секунде. Как видите, метод scheduleWithFixedDelay() весьма полезен, если мы не можем заранее сказать, сколько будет выполняться задача.
```java 
synchronized void incrementSync() {
    count = count + 1;
}
//
void incrementSync() {
    synchronized (this) {
        count = count + 1;
    }
}
```
 
 
## ThreadPoolExecutor - запуск асинхронных задач в пуле потоков
 Очень мощный и важный класс. Используется для запуска асинхронных задач в пуле потоков. Тем самым практически полностью отсутствует оверхэд на поднятие и остановку потоков. А за счет фиксируемого максимума потоков в пуле обеспечивается прогнозируемая производительность приложения. Как было ранее сказано, создавать данный пул предпочтительно через один из методов фабрики Executors. 
 Если же стандартных конфигураций будет недостаточно, то через конструкторы или сеттеры можно задать все основые параметры пула.  
 ```java 
public class MainClass {
    private static class FetchDataFromFile implements Runnable {
        private final String fileName;

        FetchDataFromFile(String fileName) {
            super();
            this.fileName = fileName;
        }

        @Override
        public void run() {
            try {
                System.out.println("Fetching data from " + fileName + " by " + Thread.currentThread().getName());
                Thread.sleep(5000); // Reading file
                System.out.println("Read file successfully: " + fileName + " by " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String getFileName() {
            return fileName;
        }
    }

    public static void main(String... args) throws InterruptedException, ExecutionException {
        // Getting instance of ThreadPoolExecutor using  Executors.newFixedThreadPool factory method
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            FetchDataFromFile fetchDataFromFile = new FetchDataFromFile("File" + i + ".temp");
            System.out.println("A new file has been added to read : " + fetchDataFromFile.getFileName());
            // Submitting task to executor
            executor.execute(fetchDataFromFile);
        }
        executor.shutdown();
    }
}
```

конструктор ThreadPoolExecutor:
```java 
public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue workQueue ,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler);
``` 
 

## ScheduledThreadPoolExecutor - запуск асинхронных задач в пуле потоков по расписанию
Метод расписания ScheduledThreadPoolExecutor может принимать задачи Callable и Runnable. Он возвращает ScheduledFuture.
Чтобы получить значение, возвращаемое потоком Callable. Нам нужно назначить время, после которого поток начнет выполнение. 
```java  
public class ScheduledThreadPoolExecutorTest {
    private static class RunnableThread implements Runnable {
        @Override
        public void run() {
            int count = 0;
            for (; count < 5; count++) {
                System.out.println("(вывод из класса RunnableThread) Runnable: " + count);
            }
        }
    }

    private static class CallableThread implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            int count = 0;
            for (; count < 5; count++) {
                System.out.println("(вывод из класса CallableThread) Callable: " + count);
            }
            return count;
        }
    }

    public static void main(String... args) throws InterruptedException, ExecutionException {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);

        /* запускаем Runnable */
        executor.execute(new RunnableThread());
        /* запускаем Callable спустя 2 секунды */
        ScheduledFuture<Integer> future = executor.schedule(new CallableThread(), 2, TimeUnit.SECONDS);

        /* получение значения из Callable */
        int response = future.get();
        System.out.println("future.get() из Callable-нити: " + response);

        /* число активных потоков */
        int activeCount = executor.getActiveCount();
        System.out.println("Число активных потоков: " + activeCount);

        /* послать сигнал завершения всем потокам */
        executor.shutdownNow();

        System.out.println("Пул с нитями остановлен? - " + executor.isShutdown());
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
(вывод из класса RunnableThread) Runnable: 0
(вывод из класса RunnableThread) Runnable: 1
(вывод из класса RunnableThread) Runnable: 2
(вывод из класса RunnableThread) Runnable: 3
(вывод из класса RunnableThread) Runnable: 4
(вывод из класса CallableThread) Callable: 0
(вывод из класса CallableThread) Callable: 1
(вывод из класса CallableThread) Callable: 2
(вывод из класса CallableThread) Callable: 3
(вывод из класса CallableThread) Callable: 4
future.get() из Callable-нити: 5
Число активных потоков: 0
Пул с нитями остановлен? - true
```


## ForkJoinPool - дробление задачи на нити и распределение их по ядрам процессора 
- Быстрее создает потоки чем ExecutorService. 
- Используется для деления/распределения задачи по процессорам/потокам. 
- Используется при нагруженных вычислениях (расчетах, сортировках...). При вычислениях, которые можно дробить.
- Если не хватает ядер для выполнения дробленной задачи, то ForkJoinPool ждет, когда освободятся ядра от вычислений, чтобы занять их и продолжить вычисления.
Еще по теме https://www.lektorium.tv/lecture/27591

```java 
public class Multithreading {
    private static class Calc {
        void go(int numberForCalc) {
            for (int i = 0; i <= numberForCalc; i++) {
                double pow = Math.pow(numberForCalc, 100);
            }
        }
    }

    /* В этом классе-наследнике производится дробление потоков вычисления за счет рекурсивного вычисления */
    private static class MyStream extends RecursiveAction {
        private final int countProcessors = Runtime.getRuntime().availableProcessors();
        private final int countLimit = 2000; // За раз считать в потоке
        private final int start;
        private final int end;

        MyStream(int startNumber, int endNumber) {
            this.start = startNumber;
            this.end = endNumber;
        }

        protected void compute() {
            if (countProcessors == 1 || end - start <= countLimit) {
                System.out.print("|");
                for (int i = start; i <= end; i++) {
                    new Calc().go(i);
                }
            } else {
                int middle = (start + end) / 2;

                /* Рекурсивно вызываем invokeAll */
                invokeAll(new MyStream(start, middle), new MyStream(middle + 1, end));
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int componentValue = 16000;
        Long begin = System.currentTimeMillis();
        {
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            MyStream myStream = new MyStream(0, componentValue);
            forkJoinPool.invoke(myStream);
        }
        System.out.println();
        System.out.println("delta time: " + (System.currentTimeMillis() - begin));
    }
}
```  
```text 
||||||||
delta time: 10843
```















## Получение данных из Executor (Callable, FutureTask, Future)

***Callable***
Callable — это также функциональный интерфейс, но, в отличие от Runnable, он может возвращать значение.
Метод get() блокируется для ожидания результата. 
```java 
public static void main(String[] args) throws Exception {
   Callable callable = () -> {
       try {
           TimeUnit.SECONDS.sleep(1);
           return 123;
       } catch (InterruptedException e) {
           throw new IllegalStateException("task interrupted", e);
       }
   };

   ExecutorService executor = Executors.newFixedThreadPool(1);
   Future<Integer> future = executor.submit(callable);
   System.out.println("future done? " + future.isDone());
   
   /* Таймаут. Любой вызов метода future.get() блокирует поток до тех пор, пока задача не будет завершена */
   Integer result = future.get(); 
     
   System.out.println("future done? " + future.isDone());
   System.out.print("result: " + result);
   executor.shutdownNow();
   future.get();
}
```
Вы, возможно, заметили, что на этот раз мы создаем сервис немного по-другому: с помощью метода newFixedThreadPool(1), который вернет исполнителя с пулом в один поток. Это эквивалентно вызову метода newSingleThreadExecutor(), однако мы можем изменить количество потоков в пуле.

***FutureTask***
```java 
FutureTask<String> ft = new FutureTask<String>(new Callable<String>() {
    @Override
    public String call() throws Exception {
        return "A";
    }
});

/* Кастомизацию (Future<String>) можно не делать */ 
Future<String> fsRes = (Future<String>) service.submit(ft);  
```

***Future***
```java 
ExecutorService service = Executors.newFixedThreadPool(2);

/* Для получение из потока значение */ 
Future<String> res = service.submit(new Callable<String>() {
    @Override
    public String call() throws Exception { // текущий поток транслирует исключения наверх
        int x = 10 / 0;
        Thread.sleep(2000);
        return "ABS";
    }
});

/* Запретить добавлять в диспетчер потоки */ 
service.shutdown();  

/* ОЖИДАНИЕ результата и получение его (get() жидает завершения потока) */
System.out.println(res.get());  
```
***ScheduledFuture***
```java 
ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
ScheduledFuture<Integer> future = executor.schedule(new CallableThread(), 2, TimeUnit.SECONDS);
```





## Обработка исключений
***исключения в Callable***
Если в Callable возникло в коде исключение, то программа прерывается.
```java 
public static void main(String... args) throws InterruptedException, ExecutionException {
    ExecutorService service = Executors.newFixedThreadPool(2);
    Future<String> future = service.submit(new Callable<String>() {
        @Override
        public String call() throws Exception {
            int x = 10 / 0;
            return "@";
        }
    });
    service.shutdown();
    System.out.println(future.get());
}
```
```text 
_______________________________
/opt/jdk/bin/java...
Exception in thread "main" java.util.concurrent.ExecutionException: java.lang.ArithmeticException: / by zero
	at java.util.concurrent.FutureTask.report(FutureTask.java:122)
	at java.util.concurrent.FutureTask.get(FutureTask.java:192)
	at ru.davidlevy.conspects.multithreading.Temp.main(MainClass.java:16)
Caused by: java.lang.ArithmeticException: / by zero
	at ru.davidlevy.conspects.multithreading.Temp$1.call(MainClass.java:11)
	at ru.davidlevy.conspects.multithreading.Temp$1.call(MainClass.java:8)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)

Process finished with exit code 1
```
***исключения в Runable, Thread***
Если в Runable или Thread возникло в коде исключение, то программа НЕ прерывается, а продолжает выполняться.


















## Синхронизация потоков

### Механизм взаимодействия потоков исполнения
в Java внедрён механизм взаимодействия потоков исполнения с помощью методов wait(), notify() и notifyAll(). 
Эти методы реализованы как завершенные в классе Object, поэтому они доступны всем классам. 
Все три метода могут быть вызваны только из синхронизированного контекста. 
Правила применения этих методов достаточно просты, хотя с точки зрения вычислительной техники они принципиально прогрессивны. 

Эти правила состоят в следующем:
- метод wait() вынуждает вызывающий поток исполнения уступить монитор и перейти в состояние ожидания до тех пор, пока какой-нибудь другой поток исполнения не войдёт в тот же монитор и не вызовет метод notify() или notifyAll();
- метод notify() возобновляет исполнение потока, из которого был вызван метод wait() для того же самого объекта;
- метод notifyAll() возобновляет исполнение всех потоков, из которых был вызван метод wait() для того же самого объекта. Одному из этих потоков предоставляется доступ.
#### Метод wait(), notify(), notifyAll()
```java  
private static class Queue {
    int number;
    boolean valueSet = false;

    synchronized int get() {
        while (!this.valueSet) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(" Пoлyчeнo : " + this.number);
        this.valueSet = false;
        notify();
        return this.number;
    }

    synchronized void put(int number) {
        while (this.valueSet) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.number = number;
        this.valueSet = true;
        System.out.println(" Oтпpaвлeнo : " + number);
        notify();
    }
}

private static class Producer implements Runnable {
    Queue queue;

    Producer(Queue queue) {
        this.queue = queue;
        new Thread(this, "Поставщик").start();
    }

    @Override
    public void run() {
        int i = 0;
        while (true)
            this.queue.put(i++);
    }
}

private static class Consumer implements Runnable {
    Queue queue;

    Consumer(Queue q) {
        this.queue = q;
        new Thread(this, " Потребитель ").start();
    }

    @Override
    public void run() {
        int i = 0;
        while (true)
            this.queue.get();
    }
}

public static void main(String[] args) {
    Queue queue = new Queue();
    new Producer(queue);
    new Consumer(queue);
}
```
```text 
_______________________________
/opt/jdk/bin/java...
 Oтпpaвлeнo : 0
 Пoлyчeнo : 0
 Oтпpaвлeнo : 1
 Пoлyчeнo : 1
 ***
 Oтпpaвлeнo : 17627
 Пoлyчeнo : 17627
 ***
 
Process finished with exit code 1
```

В приведенном ниже примере программы демонстрируется применение методов wait() и notify(), для управления выполнением потока. 
Рассмотрим подробнее работу этой программы. 
Класс NewThread содержит переменную suspendFlag, используемую для управления выполнением потока. 
Метод run() содержит блок оператора synchronized, где проверяется состояние переменной suspendFlag. 
Если она принимает логическое значение true, то вызывается метод wait() для приостановки выполнения потока. 
В методе mysuspend() устанавливается true переменной suspendFlag, а в методе myresume() - false и вызывается метод notify(), чтобы активизировать поток исполнения.
И наконец, в методе main() вызываются оба мeтoдa - mysuspend() и myresume().
```java 
public class Multithreading {
    private static class NewThread implements Runnable {
        String name;
        Thread t;
        boolean suspendFlag;

        NewThread(String name) {
            this.name = name;
            t = new Thread(this.name);
            System.out.println("Hoвый поток : " + t);
            suspendFlag = false;
            t.start();
        }

        synchronized void mysuspend() {
            suspendFlag = true;
        }

        synchronized void myresume() {
            suspendFlag = false;
            notify();
        }

        @Override
        public void run() {
            for (int i = 15; i > 0; i--)
                System.out.println(name + " - " + i);

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (suspendFlag)
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        NewThread thread1 = new NewThread("Поток 1");
        NewThread thread2 = new NewThread("Поток 2");

        Thread.sleep(1000);
        thread1.mysuspend();
        System.out.println("Пpиocтaнoвкa потока 1");
        Thread.sleep(1000);
        thread1.myresume();
        System.out.println("Boзoбнoвлeниe потока 1");
        thread2.mysuspend();
        System.out.println("Пpиocтaнoвкa потока 2");
        Thread.sleep(1000);
        thread2.myresume();
        System.out.println("Boзoбнoвлeниe потока 2 ");
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
Hoвый поток : Thread[Поток 1,5,main]
Hoвый поток : Thread[Поток 2,5,main]
Пpиocтaнoвкa потока 1
Boзoбнoвлeниe потока 1
Пpиocтaнoвкa потока 2
Boзoбнoвлeниe потока 2 

Process finished with exit code 0
```
Если запустить эту программу на выполнение, то можно увидеть, как исполнение потоков приостанавливается и возобновляется. И хотя этот механизм не так прост, как прежний, его следует всё же придерживаться, чтобы избежать ошибок во время выполнения.



В данном примере notifyAll() уведомляет все потоки о том, что с блока снята блокировка, и как следствие в блоку можно обратиться:
```java 
public class Task1 {
    /* Последовательность символов */
    private static String sequence;

    /* Количество повторений, раз */
    private static int time;

    private static class MyRunnable implements Runnable {
        private static final Object MONITOR = new Object();
        private static char nextChar = sequence.charAt(0);

        private char firstChar;
        private char secondChar;

        public MyRunnable(int index) {
            this.firstChar = sequence.charAt(index);
            this.secondChar = (index + 1 < sequence.length()) ? sequence.charAt(index + 1) : sequence.charAt(0);
        }

        @Override
        public void run() {
            synchronized (MONITOR) {
                for (int i = 0; i < time; i++) {
                    while (nextChar != this.firstChar)
                        try {
                            MONITOR.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    System.out.print(nextChar);
                    MONITOR.notifyAll();
                    nextChar = this.secondChar;
                }
            }
        }
    }

    /* Точка входа */
    public static void main(String[] args) {
        System.out.println("Создать три потока, каждый из которых выводит определенную букву (A, B и C) 5 раз, порядок должен быть именно ABCABCABCABCABC");
        System.out.print("Результат: ");

        /* Последовательность символов */
        sequence = "ABC";

        /* Количество повторений, раз */
        time = 5;

        /*
        * Количество потоков вычисляется исходя из кол-ва символов в sequence. То есть 3.
        * Каждый поток работает с одним символом из sequence, по индексу начиная с 0.
        */
        for (int i = 0, quantity = sequence.length(); i < quantity; i++)
            new Thread(new MyRunnable(i)).start();
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
Создать три потока, каждый из которых выводит определенную букву (A, B и C) 5 раз, порядок должен быть именно ABCABCABCABCABC
Результат: ABCABCABCABCABC
Process finished with exit code 0
```



### Метод join() - ожидание завершения нитей внутри main-потока 
Допустим требуется посчитать в потоке значение time и вывести его в консоль после завершения:
```java 
public class MainClass {
    private static int time = 0;
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time++;
            }
        });

        t.start();

        System.out.println(time);
    }
}
```
Данный вариант ответа нас не устраивает:
```text 
_______________________________
/opt/jdk/bin/java...
0

Process finished with exit code 0
```


Чтобы получить результат нужно в главном потоке, в потоке main, прописать метод .join():
```java 
public class MainClass throws InterruptedException {
    private static int time = 0;
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time++;
            }
        });
        t.start();

        t.join();

        System.out.println(time);
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
10

Process finished with exit code 0
```
Если потоков несколько, то для ожидания завершения объекта Thread необходимо применять метод join().

Следовательно последовательное ожидание реализуется следующим образом:
```java 
public class MainClass {
    private static int time = 0;
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time++;
            }
            System.out.println("t1 end.");
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("t2 end.");
        });

        t1.start();
        t2.start();

        try {
            t1.join();  // Ждем завершения t1
            t2.join(); // Теперь ждем завершения t2
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(time);
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
t1 end.
t2 end.
10

Process finished with exit code 0
```

### Synchronized (по методу, по монитору (объекту), по статическому методу)
- Синхронизированный метод создается путем указания ключевого слова  synchronized в его объявлении. Как только синхронизированный метод любого объекта получает управление, объект блокируется, и ни один синхронизированный метод этого объекта не может быть вызван другим потоком.
- Потоки, которым требуется синхронизированный метод, используемый другим потоком, ожидают до тех пор, пока не будет разблокирован объект, для которого он вызывается. Когда синхронизированный метод завершается, объект, для которого он вызывался, разблокировался.


```java 
public class Multithreading {
    private static final Object MONITOR = new Object();
    
    private static synchronized void methodSynchronized() {
        // монитор - сам класс!
        System.out.println(Thread.currentThread().getName() + " (старт)");
        System.out.println(" static synchronized void methodSynchronized() {...");
        System.out.println(Thread.currentThread().getName() + " (стоп)\n");
    }

    private void methodMonitor() {
        // монитор внешний
        synchronized (MONITOR) {
            System.out.println(Thread.currentThread().getName() + " (старт)");
            System.out.println(" synchronized (MONITOR) {...");
            System.out.println(Thread.currentThread().getName() + " (стоп)\n");
        }
    }

    private void methodMonitorSelf() {
        // монитор - объект main-класса
        synchronized (this) {
            System.out.println(Thread.currentThread().getName() + " (старт)");
            System.out.println(" synchronized (this) {...");
            System.out.println(Thread.currentThread().getName() + " (стоп)\n");
        }
    }

    /* Точка входа */
    public static void main(String[] args) {
        // монитор - это сам класс
        new Thread(Multithreading::methodSynchronized, "по статическому методу").start();
        new Thread(Multithreading::methodSynchronized, "по статическому методу").start();

        // монитор - объект-монитор 
        new Thread(Multithreading::methodMonitor, "по монитору").start();
        new Thread(Multithreading::methodMonitor, "по монитору").start();

        // монитор - объект main-класса
        new Thread(() -> new Multithreading().methodMonitorSelf(), "this").start();
        new Thread(() -> new Multithreading().methodMonitorSelf(), "this").start();
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
по статическому методу (старт)
 static synchronized void methodSynchronized() {...
по статическому методу (стоп)

по методу (старт)
 static synchronized void methodSynchronized() {...
по монитору (старт)
по методу (стоп)

 synchronized (MONITOR) {...
по монитору (стоп)

this (старт)
 synchronized (this) {...
this (стоп)

this (старт)
 synchronized (this) {...
this (стоп)

по монитору (старт)
 synchronized (MONITOR) {...
по монитору (стоп)

Process finished with exit code 0
```
 

Один поток увеличивает значение переменной, второй меньшает. 
```java 
public class MainClass {
    private static class MyCounter {
        private int c;

        MyCounter() {
            this.c = 0;
        }

        int value() {
            return this.c;
        }

        synchronized void inc() {
            c++;
        }

        synchronized void dec() {
            c--;
        }
    }

    public static void main(String[] args) {
        MyCounter myCounter = new MyCounter();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                myCounter.inc();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                myCounter.dec();
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(myCounter.value());
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
0

Process finished with exit code 0
```
То есть если поток t1 вошел в метод inc(), то поток t2 не сможет войти в него до тех пор, пока поток t1 не освободит метод.

Другой пример. Имеется банкомат. Три потока снимают деньги в банкомате. Проблема в том, что банк дает деньги “в кредит”.
Решение: синхронизируем блок выдачи. В данном случае монитором выступает объект класса MyATM.
```java 
public class MainClass {
    private static class MyATM {
        private int money;

        public MyATM(int money) {
            this.money = money;
        }

        synchronized void takeMoney(int amount, String name) {
            if (this.money >= amount) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.money -= amount;
                System.out.println(name + " take " + amount);
            } else {
                System.out.println(name + " - not enouth money.");
            }
        }

        void info() {
            System.out.println("ATM: " + this.money);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyATM atm = new MyATM(100);

        Thread u1 = new Thread(() -> atm.takeMoney(50, "User 1"));
        Thread u2 = new Thread(() -> atm.takeMoney(50, "User 2"));
        Thread u3 = new Thread(() -> atm.takeMoney(50, "User 3"));

        u1.start();
        u2.start();
        u3.start();

        u1.join();
        u2.join();
        u3.join();

        atm.info();
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
User 2 take 50
User 3 take 50
User 1 - not enouth money.
ATM: 0

Process finished with exit code 0

User 1 take 50
User 3 take 50
User 2 - not enouth money.
ATM: 0

Process finished with exit code 0

User 1 take 50
User 2 take 50
User 3 - not enouth money.
ATM: 0

Process finished with exit code 0
```

Эмулятор МФУ. Сначала делается печать, затем сканирование. Монитор - объект класса MFU.
```java 
public class MFU {
    private synchronized void scan(int pages) {
        try {
            System.out.println("scan start");
            Thread.sleep(1000);
            System.out.println("scan stop");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void print(int pages) {
        try {
            System.out.println("print start");
            Thread.sleep(1000);
            System.out.println("print stop");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MFU mfu = new MFU(); // mfu - монитор

        new Thread(() -> {
            mfu.print(10);
        }).start();

        new Thread(() -> {
            mfu.scan(10);
        }).start();
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
print start
print stop
scan start
scan stop

Process finished with exit code 0
```

Чтобы одновременно можно сканировать и печатать на МФУ надо создать два объекта-монитора внутри класса MFU, и теперь два разных потока одновременно не смогут сделать две печати или два сканирования, но смогут одновременно сканировать и печатать:
```java 
public class MFU {
    public static void main(String[] args) {
        MFU mfu = new MFU();

        new Thread(() -> {
            mfu.print(5);
        }).start();

        new Thread(() -> {
            mfu.scan(2);
        }).start();

        new Thread(() -> {
            mfu.scan(1);
        }).start();
    }
    
    private static final Object M_SCAN = new Object(); // монитор
    private static final Object M_PRINT = new Object(); // монитор

    private void scan(int pages) {
        synchronized (M_SCAN) {
            try {
                System.out.println("scan start for scaning " + pages + " pages");
                Thread.sleep(pages * 500);
                System.out.println("scan stop (" + pages + " pages)");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void print(int pages) {
        synchronized (M_PRINT) {
            try {
                System.out.println("print start for printing " + pages + " pages");
                Thread.sleep(pages * 500);
                System.out.println("print stop (" + pages + " pages)");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
scan start for scaning 2 pages
print start for printing 5 pages
scan stop (2 pages)
scan start for scaning 1 pages
scan stop (1 pages)
print stop (5 pages)

Process finished with exit code 0
```

#### Synchronized по внешнему монитору (по объекту класса Object; по блоку синхронизации); обрамляет кусок кода в методе
```java 
public class MainClass {
    public static void main(String[] args) {
        final Object monitor = new Object();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread1.start");
                synchronized (monitor) {
                    System.out.println("thread1.1");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("thread1.2");
                }
                System.out.println("thread1.stop");
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread2.start");
                synchronized (monitor) {
                    System.out.println("thread2.1");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("thread2.2");
                }
                System.out.println("thread2.stop");
            }
        }).start();
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
thread1.start
thread1.1
thread2.start // запускается t2 и ждет завершения 
thread1.2  // произошло завершение
thread1.stop
thread2.1 // стартует теперь t2
thread2.2
thread2.stop

Process finished with exit code 0
```

Другой пример. Два потока одновременно пишут в одну ячейку памяти:
```java 
public class MainClass {
    private static String save = ""; // сюда будем писать
    private static final Object MONITOR = new Object();
    
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (MONITOR) {
                    save += "A";
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (MONITOR) {
                    save += "B";
                }
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(save + "[" + save.length() + "]");
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
AAAABBABABABABAABBBB[20]
ABBAABABBAABABABABAB[20]
BAABABABBABAABABABAB[20]
***

Process finished with exit code 0
```
Аналогичное можно реализовать используя StringBuffer. См. далее.

### Объект класса StringBuffer
```java 
public class MainClass {
    private static StringBuffer save = new StringBuffer();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                save.append("A"); // метод append() уже синхронизирован в классе StringBuffer
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                save.append("B"); // метод append() уже синхронизирован в классе StringBuffer
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(save + "[" + save.length() + "]");
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
AAAABBABABABABAABBBB[20]
ABBAABABBAABABABABAB[20]
BAABABABBABAABABABAB[20]
***

Process finished with exit code 0
```

### По переменной
Данную синхронизацию можно реализовать:
- используя синхронизацию по монитору, см. выше;
- используя объект класса StringBuffer, см. выше;
- используя волатильную или атомарную переменную, см. далее.

#### volatile 
Данная переменная использоваться для хранения значения, которое должно быть доступно всем потокам всегда.

Свойства волотильной переменной:
- делается только одна мастер-копия и уже с ней работают все потоки; 
- переменная не блокируется и как следствие доступна для модификации разными потоками чтением и записью;
- после завершения обращения всех потоков к волатильной переменной последнее состояние мастер-копии копируется в саму переменную.
```java 
public class Multithreading {
    private static volatile int valueVolatile = 0;

    private static void method1() {
        System.out.println(Thread.currentThread().getName() + " (старт)");
        valueVolatile += 1;
        System.out.println(" " + valueVolatile);
        System.out.println(Thread.currentThread().getName() + " (стоп)");
    }

    private static void method2() {
        System.out.println(Thread.currentThread().getName() + " (старт)");
        valueVolatile += 1000;
        System.out.println(" " + valueVolatile);
        System.out.println(Thread.currentThread().getName() + " (стоп)");
    }

    public static void main(String[] args) {
        System.out.println("До изменения валатильной переменной: " + valueVolatile);
        new Thread(Multithreading::method1).start();
        new Thread(Multithreading::method2).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("После изменений: " + valueVolatile);
    }
} 
```
```text 
_______________________________
/opt/jdk/bin/java...
До изменения валатильной переменной: 0
Thread-0 (старт)
 1
Thread-0 (стоп)
Thread-1 (старт)
 1001
Thread-1 (стоп)
После изменений: 1001

Process finished with exit code 0
```

#### java.util.concurrent.atomic
- Операция называется атомарной тогда, когда её можно безопасно выполнять при параллельных вычислениях в нескольких потоках, не используя при этом ни блокировок, ни synchronized.
- Внутри атомарные классы очень активно используют сравнение с обменом (compare-and-swap, CAS), атомарную инструкцию, которую поддерживает большинство современных процессоров. Эти инструкции работают гораздо быстрее, чем синхронизация с помощью блокировок. 
Поэтому, если вам просто нужно изменять одну переменную с помощью нескольких потоков, лучше выбирать атомарные классы.

#### AtomicInteger
```java  
AtomicInteger atomic = new AtomicInteger(0);
ExecutorService service = Executors.newFixedThreadPool(2);
IntStream.range(0, 1000).forEach(i -> service.submit(atomic::incrementAndGet));
service.shutdown();
/* <= 1000 */
System.out.println(atomic.get());
```
Как видите, использование AtomicInteger вместо обычного Integer позволило нам корректно увеличить число, распределив работу сразу по двум потокам. 
Мы можем не беспокоиться о безопасности, потому что incrementAndGet() является атомарной операцией.

Класс AtomicInteger поддерживает много разных атомарных операций. Метод updateAndGet() принимает в качестве аргумента лямбда-выражение и выполняет над числом заданные арифметические операции:
```java 
AtomicInteger atomic = new AtomicInteger(0);
ExecutorService service = Executors.newFixedThreadPool(2);
IntStream.range(0, 1000)
        .forEach(i -> {
            Runnable task = () ->
                    atomic.updateAndGet(n -> n + 2);
            service.submit(task);
        });
service.shutdown();
/* <= 2000 */
System.out.println(atomic.get());
```

Метод accumulateAndGet() принимает лямбда-выражения типа IntBinaryOperator. Вот как мы можем использовать его, чтобы просуммировать все числа от нуля до тысячи:
```java 
AtomicInteger atomic = new AtomicInteger(0);
ExecutorService service = Executors.newFixedThreadPool(2);
IntStream.range(0, 1000)
        .forEach(i -> {
            Runnable task = () ->
                    atomic.accumulateAndGet(i, (n, m) -> n + m);
            service.submit(task);
        });
service.shutdown();
/* <= 499500 */
System.out.println(atomic.get());
```

Среди других атомарных классов хочется упомянуть такие как AtomicBoolean, AtomicLong и AtomicReference.

#### LongAdder
Класс LongAdder может выступать в качестве альтернативы AtomicLong для последовательного сложения чисел.
```java 
LongAdder longAdder = new LongAdder();
ExecutorService executor = Executors.newFixedThreadPool(2);
IntStream.range(0, 1000).forEach(i -> executor.submit(longAdder::increment));
executor.shutdown();
/* <= 1000 */
System.out.println(longAdder.sumThenReset());
```
Так же, как и у других атомарных чисел, у LongAdder есть методы increment() и add(). Но вместо того, чтобы складывать числа сразу, он просто хранит у себя набор слагаемых, чтобы уменьшить взаимодействие между потоками. Узнать результат можно с помощью вызова sum() или sumThenReset(). 
Этот класс используется в ситуациях, когда добавлять числа приходится гораздо чаще, чем запрашивать результат (часто это какие-то статистические исследование, например подсчёт количества запросов). Несложно догадаться, что, давая прирост в производительности, LongAdder требует гораздо большего количества памяти из-за того, что он хранит все слагаемые.

#### LongAccumulator
Класс LongAccumulator несколько расширяет возможности LongAdder. Вместо простого сложения он обрабатывает входящие значения с помощью лямбды типа LongBinaryOperator, которая передаётся при инициализации. Выглядит это так:
```java 
LongBinaryOperator longBinaryOperator = (x, y) -> x + 2 * y;
LongAccumulator longAccumulator = new LongAccumulator(longBinaryOperator, 1L);
ExecutorService service = Executors.newFixedThreadPool(2);
IntStream.range(0, 10).forEach(i -> service.submit(() -> longAccumulator.accumulate(i)));
service.shutdown();
/* <= 2539 */
System.out.println(longAccumulator.getThenReset());
```
В этом примере при каждом вызове accumulate() значение аккумулятора увеличивается в два раза, и лишь затем суммируется с i. 
Так же, как и LongAdder, LongAccumulator хранит весь набор переданных значений в памяти.
LongAccumulator не гарантирует порядка выполнения операций. 












### Синхронизаторы из библиотеки java.util.concurrent

#### Semaphore управляют пропускной способностью (ограничение по блоку)
визуализация  https://habrastorage.org/files/9da/48f/85b/9da48f85b5874362bc2279f181613c0e.gif

Визуализация:
- ограничение по пропускной способности
- выполняться могут именно n потоков не более и не менее

Существует парковка, которая одновременно может вмещать не более 5 автомобилей. Если парковка заполнена полностью, то вновь прибывший автомобиль должен подождать пока не освободится хотя бы одно место. После этого он сможет припарковаться.
Семафор отлично подходит для решения подобной задачи: он не дает автомобилю (потоку) припарковаться (зайти в заданный блок кода и воспользоваться общим ресурсом) если мест на парковке нет.

Синхронизатор Semaphore реализует шаблон синхронизации Семафор. Чаще всего, семафоры необходимы, когда нужно ограничить доступ к некоторому общему ресурсу. В конструктор этого класса (Semaphore(int permits) или Semaphore(int permits, boolean fair)) обязательно передается количество потоков, которому семафор будет разрешать одновременно использовать заданный ресурс.


Стоит отметить, что класс Semaphore поддерживает захват и освобождение более чем одного разрешения за раз, но в данном задаче это не нужно. 

* метод acquire() - начало блока захвата; закрывает код от других потоков
* метод release() - конец блока захвата

Пример из жизни: парковка машины на стоянке 

```java 
public static void main(String[] args) {
    /* параллельно можно запускать только 5 потоков */
    final Semaphore semaphore = new Semaphore(5);
    
    /* пул из 10 потоков */
    ExecutorService service = Executors.newFixedThreadPool(10);
    
    /* добавим 10 задач */
    for (int i = 0; i < 10; i++) {
        final int w = i;
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    /* овладеть */
                    semaphore.acquire();


                    System.out.println(w + "-з");
                    Thread.sleep(2000);
                    System.out.println(w + "-end");


                    /* отпустить */
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    service.shutdown();
}
```
```text 
_______________________________
/opt/jdk/bin/java...
3-start
0-start
7-start
1-start
4-start
3-end
5-start
0-end
8-start
7-end
9-start
1-end
2-start
4-end
6-start
5-end
8-end
9-end
2-end
6-end

Process finished with exit code 0
```

До блока семафоров потоки могут запуститься и отработать:

```java 
public static void main(String[] args) {
    class MyTask {
        void method(int n) {
            try {
                System.out.println("parallel task-start" + n);
                Thread.sleep(2000);
                System.out.println("parallel task-end" + n);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    MyTask myTask = new MyTask();

    Semaphore semaphore = new Semaphore(5);

    ExecutorService service = Executors.newFixedThreadPool(15);

    for (int i = 0; i < 15; i++) {
        final int w = i;
        service.execute(() -> {
            try {
                /* до блока семафоров потоки могут запуститься и отработать */
                myTask.method(w);

                /* овладеть */
                semaphore.acquire();

                System.out.println(w + "-start");
                Thread.sleep(2000);
                System.out.println(w + "-end");

                /* отпустить */
                semaphore.release();

                myTask.method(w);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
    
    service.shutdown();
}
```
```text 
_______________________________
/opt/jdk/bin/java...
parallel task-start1
parallel task-start2
parallel task-start3
parallel task-start5
parallel task-start6
parallel task-start0
parallel task-start11
parallel task-start4
parallel task-start13
parallel task-start8
parallel task-start7
parallel task-start9
parallel task-start10
parallel task-start14
parallel task-start12
parallel task-end1
1-start
parallel task-end2
2-start
parallel task-end3
3-start
parallel task-end5
5-start
parallel task-end6
6-start
parallel task-end0
parallel task-end11
parallel task-end4
parallel task-end13
parallel task-end8
parallel task-end9
parallel task-end7
parallel task-end10
parallel task-end14
parallel task-end12
1-end
3-end
2-end
parallel task-start2
parallel task-start1
0-start
4-start
parallel task-start3
11-start
5-end
parallel task-start5
13-start
6-end
parallel task-start6
8-start
parallel task-end2
parallel task-end1
13-end
9-start
parallel task-start13
parallel task-end5
0-end
parallel task-start0
7-start
4-end
10-start
parallel task-start4
parallel task-end3
11-end
14-start
parallel task-start11
parallel task-end6
8-end
parallel task-start8
12-start
parallel task-end0
9-end
parallel task-start9
parallel task-end13
10-end
parallel task-start10
7-end
parallel task-start7
parallel task-end4
14-end
parallel task-end11
parallel task-start14
parallel task-end8
12-end
parallel task-start12
parallel task-end7
parallel task-end9
parallel task-end10
parallel task-end14
parallel task-end12

Process finished with exit code 0
```

#### CountDownLatch
визуализация https://habrastorage.org/files/46b/3ae/b41/46b3aeb417cf4fb4ba271b4c66b52436.gif 

CountDownLatch (замок с обратным отсчетом) предоставляет возможность любому количеству потоков в блоке кода ожидать до тех пор, пока не завершится определенное количество операций, выполняющихся в других потоках, перед тем как они будут «отпущены», чтобы продолжить свою деятельность. 
В конструктор CountDownLatch (CountDownLatch(int count)) обязательно передается количество операций, которое должно быть выполнено, чтобы замок «отпустил» заблокированные потоки.

Блокировка потоков снимается с помощью счётчика: любой действующий поток, при выполнении определенной операции уменьшает значение счётчика. 
Когда счётчик достигает 0, все ожидающие потоки разблокируются и продолжают выполняться.

* метод countDown() - уменьшат значение счетчика 
* метод await() - ждет, когда счетчик станет равным 0

Пример из жизни "экскурсионная группа" то есть, пока не наберется определенное количество человек, экскурсия не начнется.
```java 
public static void main(String[] args) throws InterruptedException {
    /* 20 потоков будут контролироваться */
    CountDownLatch countDownLatch = new CountDownLatch(20);
   
    /* пул из 10 потоков */
    ExecutorService service = Executors.newFixedThreadPool(10);
    
    /* организуем 20 потоков */
    for (int i = 0; i < 20; i++) {
        final int w = i;
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(w + "-start");
                    Thread.sleep(2000);
                    System.out.println(w + "-end");

                    /* уменьшить на 1 */
                    countDownLatch.countDown();
                    
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    service.shutdown();
    
    /* когда защелка дойдет до нуля */
    countDownLatch.await();
    
    System.out.println("MainClass out");
}
```
```text 
_______________________________
/opt/jdk/bin/java...
0-start
2-start
3-start
1-start
5-start
6-start
7-start
4-start
9-start
8-start
2-end
10-start
3-end
1-end
12-start
0-end
6-end
11-start
5-end
15-start
14-start
13-start
9-end
16-start
7-end
17-start
4-end
18-start
8-end
19-start
12-end
10-end
14-end
15-end
13-end
11-end
16-end
17-end
19-end
18-end
MainClass out

Process finished with exit code 0
```
еще пример
```java 
    public static void main(String[] args) throws InterruptedException {
        /* задаем кол-во потоков */
        final int THREADS_COUNT = 4;
        
        /* задаем значение счетчика */
        CountDownLatch countDownLatch = new CountDownLatch(THREADS_COUNT);
        
        System.out.println("Старт");
        for (int i = 0; i < THREADS_COUNT; i++) {
            final int w = i;
            new Thread(() -> {
                try {
                    /* считаем что выполнение задачи занимает 2 сек */
                    Thread.sleep(2000 * w + (int) (5000 * Math.random()));

                    /* как только задача выполнена, уменьшаем счетчик */
                    countDownLatch.countDown();

                    System.out.println("ПОТОК #" + w + " - готов");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        /* ждем пока счетчик не сбросится в ноль, пока это не произойдет, будем стоять на этой строке */
        countDownLatch.await();

        System.out.println("Стоп"); 
    }
```
```text 
_______________________________
/opt/jdk/bin/java...
Старт
ПОТОК #0 - готов
ПОТОК #1 - готов
ПОТОК #3 - готов
ПОТОК #2 - готов
Стоп

Process finished with exit code 0
```

с атомарной переменной; здесь 5 потоков последовательно прибавляют по 20 единиц к атомарной переменной 
```java 
public class MainClass {
    private static AtomicInteger atomic = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        /* кол-во потоков */
        final int THREADS_COUNT = 5;

        /* количество "тиков" в каждом потоке */
        final int TICKS_COUNT = 20;

        CountDownLatch countDownLatch = new CountDownLatch(THREADS_COUNT);

        for (int i = 0; i < THREADS_COUNT; i++) {
            new Thread(() -> {
                for (int j = 0; j < TICKS_COUNT; j++) {
                    /* усыпляем поток на 3 мс */
                    try {
                        Thread.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                    /* берем и увеличиваем значение AtomicInteger на 1 */
                    atomic.getAndAdd(1);
                }

                /* как только поток всё посчитал, то уменьшаем счетчик */
                countDownLatch.countDown();

            }).start();
        }

        /* ждем пока все потоки выполнят все действия */
        countDownLatch.await();

        /* гарантированно получим THREADS_COUNT * TICKS_COUNT */
        System.out.println("atomic: " + atomic);
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
atomic: 100

Process finished with exit code 0
```

#### CyclicBarrier
визуализация https://habrastorage.org/files/89a/f0c/b71/89af0cb71aad4465bb9c934b8be91a67.gif 

Существует паромная переправа. Паром может переправлять одновременно по три автомобиля. Чтобы не гонять паром лишний раз, нужно отправлять его, когда у переправы соберется минимум три автомобиля.

CyclicBarrier реализует шаблон синхронизации Барьер. Циклический барьер является точкой синхронизации, в которой указанное количество параллельных потоков встречается и блокируется. Как только все потоки прибыли, выполняется опционное действие (или не выполняется, если барьер был инициализирован без него), и, после того, как оно выполнено, барьер ломается и ожидающие потоки «освобождаются». В конструктор барьера (CyclicBarrier(int parties) и CyclicBarrier(int parties, Runnable barrierAction)) обязательно передается количество сторон, которые должны «встретиться», и, опционально, действие, которое должно произойти, когда стороны встретились, но перед тем когда они будут «отпущены». 

* метод await() ожидает когда все заявленные потоки (см. new CyclicBarrier(кол-во потоков)) дойдут до барьера, до метода await(), а затем все потоки одновлеменно продолжают работать. 
* конструктор CyclicBarrier(int parties, Runnable barrierAction)) выполняет barrierAction, когда барьер падает.

Пример из жизни: скачки

```java 
public class Multithreading {
    public static void main(String[] args) throws InterruptedException {
        /* на 3 потока */
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    System.out.println("H1 ready");

                    /* начинаем ожидать, когда все 3 потока перейдут в режим ожидания */
                    cyclicBarrier.await();

                    System.out.println("H1 run");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    System.out.println("H2 ready");

                    /* начинаем ожидать, когда все 3 потока перейдут в режим ожидания */
                    cyclicBarrier.await();

                    System.out.println("H2 run");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1200);
                    System.out.println("H3 ready");

                    /* начинаем ожидать, когда все 3 потока перейдут в режим ожидания */
                    cyclicBarrier.await();

                    System.out.println("H3 run");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
H1 ready
H3 ready
H2 ready
H2 run  // на этом месте потоки стартуют одновременно
H1 run
H3 run

Process finished with exit code 0
```


#### Exchanger<V>
визуализация https://habrastorage.org/files/947/ef3/f47/947ef3f47ff843a099059006b30ea54d.gif 

https://habrahabr.ru/post/277669/

Exchanger (обменник) может понадобиться, для того, чтобы обменяться данными между двумя потоками в определенной точки работы обоих потоков. Обменник — обобщенный класс, он параметризируется типом объекта для передачи.

Обменник является точкой синхронизации пары потоков: поток, вызывающий у обменника метод exchange() блокируется и ждет другой поток. Когда другой поток вызовет тот же метод, произойдет обмен объектами: каждая из них получит аргумент другой в методе exchange(). Стоит отметить, что обменник поддерживает передачу null значения. Это дает возможность использовать его для передачи объекта в одну сторону, или, просто как точку синхронизации двух потоков.

Пример. Есть два грузовика: один едет из пункта A в пункт D, другой из пункта B в пункт С. Дороги AD и BC пересекаются в пункте E. Из пунктов A и B нужно доставить посылки в пункты C и D. Для этого грузовики в пункте E должны встретиться и обменяться соответствующими посылками.
```java 
public class Delivery {
    /* Создаем обменник, который будет обмениваться типом String */
    private static final Exchanger<String> EXCHANGER = new Exchanger<>();

    public static class Truck implements Runnable {
        private int number;
        private String dep;
        private String dest;
        private String[] parcels;

        Truck(int number, String departure, String destination, String[] parcels) {
            this.number = number;
            this.dep = departure;
            this.dest = destination;
            this.parcels = parcels;
        }

        @Override
        public void run() {
            try {
                System.out.printf("В грузовик №%d погрузили: %s и %s.\n", number, parcels[0], parcels[1]);
                System.out.printf("Грузовик №%d выехал из пункта %s в пункт %s.\n", number, dep, dest);
                Thread.sleep(1000 + (long) (Math.random() * 5000));
                System.out.printf("Грузовик №%d приехал в пункт Е.\n", number);
                parcels[1] = EXCHANGER.exchange(parcels[1]);//При вызове exchange() поток блокируется и ждет
                //пока другой поток вызовет exchange(), после этого произойдет обмен посылками
                System.out.printf("В грузовик №%d переместили посылку для пункта %s.\n", number, dest);
                Thread.sleep(1000 + (long) (Math.random() * 5000));
                System.out.printf("Грузовик №%d приехал в %s и доставил: %s и %s.\n", number, dest, parcels[0], parcels[1]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        /* Формируем груз для 1-го грузовика */
        String[] p1 = new String[]{"{посылка A->D}", "{посылка A->C}"};
        /* Формируем груз для 2-го грузовика */
        String[] p2 = new String[]{"{посылка B->C}", "{посылка B->D}"};

        /* Отправляем 1-й грузовик из А в D */
        new Thread(new Truck(1, "A", "D", p1)).start();
        Thread.sleep(100);
        /* Отправляем 2-й грузовик из В в С */
        new Thread(new Truck(2, "B", "C", p2)).start();
    }
} 
```
```text 
_______________________________
/opt/jdk/bin/java...
В грузовик №1 погрузили: {посылка A->D} и {посылка A->C}.
Грузовик №1 выехал из пункта A в пункт D.
В грузовик №2 погрузили: {посылка B->C} и {посылка B->D}.
Грузовик №2 выехал из пункта B в пункт C.
Грузовик №1 приехал в пункт Е.
Грузовик №2 приехал в пункт Е.
В грузовик №2 переместили посылку для пункта C.
В грузовик №1 переместили посылку для пункта D.
Грузовик №1 приехал в D и доставил: {посылка A->D} и {посылка B->D}.
Грузовик №2 приехал в C и доставил: {посылка B->C} и {посылка A->C}.

Process finished with exit code 0
```
Как мы видим, когда один грузовик (один поток) приезжает в пункт Е (достигает точки синхронизации), он ждет пока другой грузовик (другой поток) приедет в пункт Е (достигнет точки синхронизации). После этого происходит обмен посылками (String) и оба грузовика (потока) продолжают свой путь (работу).

#### Phaser
визуализация https://habrastorage.org/files/086/6a4/b7a/0866a4b7acdf416384d4e7372b49a34b.gif 

https://habrahabr.ru/post/277669/

Phaser (фазер), как и CyclicBarrier, является реализацией шаблона синхронизации Барьер, но, в отличии от CyclicBarrier, предоставляет больше гибкости. Этот класс позволяет синхронизировать потоки, представляющие отдельную фазу или стадию выполнения общего действия. Как и CyclicBarrier, Phaser является точкой синхронизации, в которой встречаются потоки-участники. Когда все стороны прибыли, Phaser переходит к следующей фазе и снова ожидает ее завершения. 

Если сравнить Phaser и CyclicBarrier, то можно выделить следующие важные особенности Phaser:
* Каждая фаза (цикл синхронизации) имеет номер;
* Количество сторон-участников жестко не задано и может меняться: поток может регистрироваться в качестве участника и отменять свое участие;
* Участник не обязан ожидать, пока все остальные участники соберутся на барьере. Чтобы продолжить свою работу достаточно сообщить о своем прибытии;
* Случайные свидетели могут следить за активностью в барьере;
* Поток может и не быть стороной-участником барьера, чтобы ожидать его преодоления;
* У фазера нет опционального действия.

Объект Phaser создается с помощью одного из конструкторов: Phaser() и Phaser(int parties).

Параметр parties указывает на количество сторон-участников, которые будут выполнять фазы действия. Первый конструктор создает объект Phaser без каких-либо сторон, при этом барьер в этом случае тоже «закрыт». Второй конструктор регистрирует передаваемое в конструктор количество сторон. Барьер открывается когда все стороны прибыли, или, если снимается последний участник. (У класса Phaser еще есть конструкторы, в которые передается родительский объект Phaser, но мы их рассматривать не будем.)

Основные методы:
* int register() — регистрирует нового участника, который выполняет фазы. Возвращает номер текущей фазы;
* int getPhase() — возвращает номер текущей фазы;
* int arriveAndAwaitAdvance() — указывает что поток завершил выполнение фазы. Поток приостанавливается до момента, пока все остальные стороны не закончат выполнять данную фазу. Точный аналог CyclicBarrier.await(). Возвращает номер текущей фазы;
* int arrive() — сообщает, что сторона завершила фазу, и возвращает номер фазы. При вызове данного метода поток не приостанавливается, а продолжает выполнятся;
* int arriveAndDeregister() — сообщает о завершении всех фаз стороной и снимает ее с регистрации. Возвращает номер текущей фазы;
* int awaitAdvance(int phase) — если phase равно номеру текущей фазы, приостанавливает вызвавший его поток до её окончания. В противном случае сразу возвращает аргумент.

Пример: Есть пять остановок. На первых четырех из них могут стоять пассажиры и ждать автобуса. Автобус выезжает из парка и останавливается на каждой остановке на некоторое время. После конечной остановки автобус едет в парк. Нам нужно забрать пассажиров и высадить их на нужных остановках.
```java 
public class Bus {
    /* Сразу регистрируем главный поток */
    /* Фазы 0 и 6 - это автобусный парк, 1 - 5 остановки */
    private static final Phaser PHASER = new Phaser(1);

    public static class Passenger extends Thread {
        private int departure;
        private int destination;

        Passenger(int departure, int destination) {
            this.departure = departure;
            this.destination = destination;
            System.out.println(this + " ждёт на остановке № " + this.departure);
        }

        @Override
        public void run() {
            try {
                System.out.println(this + " сел в автобус.");

                /* Пока автобус не приедет на нужную остановку(фазу) */
                while (PHASER.getPhase() < destination)
                    PHASER.arriveAndAwaitAdvance();     //заявляем в каждой фазе о готовности и ждем

                Thread.sleep(1);
                System.out.println(this + " покинул автобус.");

                /* Отменяем регистрацию на нужной фазе */
                PHASER.arriveAndDeregister();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return "Пассажир {" + departure + " -> " + destination + '}';
        }
    }

    /* Точка входа */
    public static void main(String[] args) throws InterruptedException {
        ArrayList<Passenger> passengers = new ArrayList<>();

        /* Сгенерируем пассажиров на остановках */
        for (int i = 1; i < 5; i++) {
            if ((int) (Math.random() * 2) > 0)
                /* Этот пассажир выходит на следующей */
                passengers.add(new Passenger(i, i + 1));
            if ((int) (Math.random() * 2) > 0)
                /* Этот пассажир выходит на конечной */
                passengers.add(new Passenger(i, 5));
        }

        for (int i = 0; i < 7; i++) {
            switch (i) {
                case 0:
                    System.out.println("Автобус выехал из парка.");
                    /* В фазе 0 всего 1 участник - автобус */
                    PHASER.arrive();
                    break;
                case 6:
                    System.out.println("Автобус уехал в парк.");
                    /* Снимаем главный поток, ломаем барьер */
                    PHASER.arriveAndDeregister();
                    break;
                default:
                    int currentBusStop = PHASER.getPhase();
                    System.out.println("Остановка № " + currentBusStop);

                    /* Проверяем, есть ли пассажиры на остановке */
                    for (Passenger p : passengers)
                        if (p.departure == currentBusStop) {
                            /* Регистрируем поток, который будет участвовать в фазах */
                            PHASER.register();
                            /* и запускаем */
                            p.start();
                        }

                    /* Сообщаем о своей готовности */
                    PHASER.arriveAndAwaitAdvance();
            }
        }
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
Пассажир {1 -> 2} ждёт на остановке № 1
Пассажир {2 -> 5} ждёт на остановке № 2
Пассажир {3 -> 4} ждёт на остановке № 3
Автобус выехал из парка.
Остановка № 1
Пассажир {1 -> 2} сел в автобус.
Остановка № 2
Пассажир {1 -> 2} покинул автобус.
Пассажир {2 -> 5} сел в автобус.
Остановка № 3
Пассажир {3 -> 4} сел в автобус.
Остановка № 4
Пассажир {3 -> 4} покинул автобус.
Остановка № 5
Пассажир {2 -> 5} покинул автобус.
Автобус уехал в парк.

Process finished with exit code 0
```

Кстати, функционалом фазера можно воспроизвести работу CountDownLatch:
```java 
public class NewRace {
    private static final Phaser START = new Phaser(8);
    private static final int trackLength = 500000;

    public static class Car implements Runnable {
        private int carNumber;
        private int carSpeed;

        public Car(int carNumber, int carSpeed) {
            this.carNumber = carNumber;
            this.carSpeed = carSpeed;
        }

        @Override
        public void run() {
            try {
                System.out.printf("Автомобиль №%d подъехал к стартовой прямой.\n", carNumber);
                START.arriveAndDeregister();
                START.awaitAdvance(0);
                Thread.sleep(trackLength / carSpeed);
                System.out.printf("Автомобиль №%d финишировал!\n", carNumber);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /* Точка входа */
    public static void main(String[] args) throws InterruptedException {
        for (int i = 1; i <= 5; i++) {
            new Thread(new Car(i, (int) (Math.random() * 100 + 50))).start();
            Thread.sleep(100);
        }

        /* Проверяем, собрались ли все автомобили у стартовой прямой. Если нет, ждем 100ms */
        while (START.getRegisteredParties() > 3)
            Thread.sleep(100);
        Thread.sleep(100);
        
        System.out.println("На старт!");
        START.arriveAndDeregister();
        Thread.sleep(100);
        
        System.out.println("Внимание!");
        START.arriveAndDeregister();
        Thread.sleep(100);
        
        System.out.println("Марш!");
        START.arriveAndDeregister();
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
Автомобиль №1 подъехал к стартовой прямой.
Автомобиль №2 подъехал к стартовой прямой.
Автомобиль №3 подъехал к стартовой прямой.
Автомобиль №4 подъехал к стартовой прямой.
Автомобиль №5 подъехал к стартовой прямой.
На старт!
Внимание!
Марш!
Автомобиль №2 финишировал!
Автомобиль №4 финишировал!
Автомобиль №5 финишировал!
Автомобиль №3 финишировал!
Автомобиль №1 финишировал!

Process finished with exit code 0
```
???? 01:52:00

#### ReentrantLock
Как правило, для работы с блокировками используется класс ReentrantLock из пакета java.util.concurrent.locks. Данный класс реализует интерфейс Lock. 

* метод lock() ожидает, пока не будет получена блокировка и блокирует.
* метод unlock() снимает блокировку
* метод boolean tryLock() пытается получить блокировку, если блокировка получена, то возвращает true. Если блокировка не получена, то возвращает false. В отличие от метода lock() не ожидает получения блокировки, если она недоступна.
* Condition newCondition() возвращает объект Condition, который связан с текущей блокировкой. Объект Condition позволяет управлять блокировкой.

Организация блокировки в общем случае довольно проста: для получения блокировки вызывается метод lock(), а после окончания работы с общими ресурсами вызывается метод unlock(), который снимает блокировку.

Пример: если надо в одном классе выполнить блокировку в одном методе, а разблокировку в другом:
```java 
public class Multithreading {
    private static class TestClass {
        private ReentrantLock reentrantLock;

        TestClass(ReentrantLock reentrantLock) {
            this.reentrantLock = reentrantLock;
        }

        /* в это методе будем блокировать */
        void methodA() {
            try {
                /* блокируем */
                this.reentrantLock.lock();

                System.out.println("methodA - заблокировали");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /* если unlock() не сделать, то поток перейдет в вечное ожидание */
        void methodB() {
            try {
                Thread.sleep(1000);
                System.out.println("methodB - сняли блокировку");

                /* разблокируем */
                this.reentrantLock.unlock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        /* разнесем синхронизацию по разным методам */
        ReentrantLock reentrantLock = new ReentrantLock();

        TestClass c = new TestClass(reentrantLock);

        new Thread(new Runnable() {
            @Override
            public void run() {
                c.methodA();
                c.methodB();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                c.methodA();
                c.methodB();
            }
        }).start();
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
methodA - заблокировали
methodB - сняли блокировку
methodA - заблокировали
methodB - сняли блокировку

Process finished with exit code 0
```

Рассмотрим синхронизацию с попыткой заблокировать блок reentrantLock.tryLock() то есть, у нас 2 потока, если “первый” попытается заблокировать блок кода и у него это получится, то он выполнится; а вот “второму” потоку это не удастся и он просто завершится.
Можно защелку сделать со временем ожидания для блокировки блока - reentrantLock.tryLock(5, TimeUnit.SECONDS) при этом “первый” войдет в блок и отработает, “второй” подождем 5 секунд и попытается войти в освободившийся блок и сделать свою работу.
```java 
public class Multithreading {
    private static class TestClass {
        private ReentrantLock reentrantLock;

        TestClass(ReentrantLock reentrantLock) {
            this.reentrantLock = reentrantLock;
        }

        void method(String marker) {
            /* reentrantLock.tryLock()  или  reentrantLock.tryLock(5, TimeUnit.SECONDS) */
            if (this.reentrantLock.tryLock()) {
                System.out.println("method - заблокирован: " + marker);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    this.reentrantLock.unlock();
                }
            }
            System.out.println("method - выход: " + marker);
        }
    }

    public static void main(String[] args) {
        /* разнесем синхронизацию по разным методам */
        ReentrantLock reentrantLock = new ReentrantLock();

        TestClass testClass = new TestClass(reentrantLock);

        new Thread(new Runnable() {
            @Override
            public void run() {
                testClass.method("one");
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                testClass.method("two");
            }
        }).start();
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
method - заблокирован: one
method - выход: two   // two попытался войти, но у него это не получилось потому, что one уже блокирнул метод
method - выход: one

Process finished with exit code 0
```

#### ReentrantReadWriteLock (ReadWriteLock)
ReentrantReadWriteLock имеет замок на чтение и замок на запись.

Когда стоит замок:
- на чтение (кол-во потоков для чтения не ограничено), то ни один поток не может заплокировать поток для записи. 
На чтение всегда могут работать несколько потоков.
- на запись, то никто не сможет прочитать. 
На запись всегда работает только 1 поток.

Например: работа с БД или коллекцией; когда у нас есть много данных для записи/чтения, то при множественном чтении ничего страшного не произойдет, а вот при записи надо блокировать данные на чтение, чтобы записать. 
```java 
public static void main(String[] args) {
    ReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

    /* создадим 10 потоков для печати буквы А */
    for (int i = 0; i < 10; i++) {
        final int num = i;
        new Thread(new Runnable() {
            @Override
            public void run() {
                /* заблокируем блок на чтение */
                reentrantReadWriteLock.readLock().lock();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Поток № " + num + " ...");

                /* разблокируем блок на чтение */
                reentrantReadWriteLock.readLock().unlock();
            }
        }).start();
    }

    new Thread(new Runnable() {
        @Override
        public void run() {
            /* заблокируем блок на запись */
            reentrantReadWriteLock.writeLock().lock();

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Поток B1___");

            /* разблокируем блок на запись */
            reentrantReadWriteLock.writeLock().unlock();
        }
    }).start();

    new Thread(new Runnable() {
        @Override
        public void run() {
            /* заблокируем блок на запись */
            reentrantReadWriteLock.writeLock().lock();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Поток B2______");

            /* разблокируем блок на запись */
            reentrantReadWriteLock.writeLock().unlock();
        }
    }).start();
}
```
```text 
_______________________________
/opt/jdk/bin/java...
Поток № 0
Поток № 9
Поток № 1
Поток № 5
Поток № 3
Поток № 7
Поток № 4
Поток № 8
Поток № 2
Поток № 6
B1
B2

Process finished with exit code 0
```

Интерфейс ReadWriteLock предлагает другой тип блокировок — отдельную для чтения, и отдельную для записи. Этот интерфейс был добавлен из соображения, что считывать данные (любому количеству потоков) безопасно до тех пор, пока ни один из них не изменяет переменную. Таки образом, блокировку для чтения (read-lock) может удерживать любое количество потоков до тех пор, пока не удерживает блокировка для записи (write-lock). Такой подход может увеличить производительность в случае, когда чтение используется гораздо чаще, чем запись.
```java 
ExecutorService executor = Executors.newFixedThreadPool(2);
Map<String, String> map = new HashMap<>();
ReadWriteLock lock = new ReentrantReadWriteLock();
 
executor.submit(() -> {
    lock.writeLock().lock();
    try {
        sleep(1);
        map.put("foo", "bar");
    } finally {
        lock.writeLock().unlock();
    }
});
```
В примере выше мы можем видеть, как поток блокирует ресурсы для записи, после чего ждёт одну секунду, записывает данные в HashMap и освобождает ресурсы. Предположим, что в это же время были созданы ещё два потока, которые хотят получить из хэш-таблицы значение:
```java 
Runnable readTask = () -> {
    lock.readLock().lock();
    try {
        System.out.println(map.get("foo"));
        sleep(1);
    } finally {
        lock.readLock().unlock();
    }
};
 
executor.submit(readTask);
executor.submit(readTask);
 
executor.shutdown();
```
Если вы попробуете запустить этот пример, то заметите, что оба потока, созданные для чтения, будут простаивать секунду, ожидая завершения работы потока для записи. После снятия блокировки они выполнятся параллельно, и одновременно запишут результат в консоль. Им не нужно ждать завершения работы друг друга, потому что выполнять одновременное чтение вполне безопасно (до тех пор, пока ни один поток не работает параллельно на запись).







#### StampedLock
В Java 8 появился новый тип блокировок — StampedLock. Так же, как и в предыдущих примерах, он поддерживает разделение на readLock() и writeLock(). Однако, в отличие от ReadWriteLock, метод блокировки StampedLock возвращает «штамп» — значение типа long. Этот штамп может использоваться в дальнейшем как для высвобождения ресурсов, так и для проверки состояния блокировки. Вдобавок, у этого класса есть методы, для реализации «оптимистичной» блокировки. Но обо всём по порядку.

* long writeLock(), unlockWrite(stamp) - на запись блокировка/разблокировка
* long readLock(), unlockRead(stamp) - на чтение блокировка/разблокировка 
* long tryOptimisticRead() - оптимистичная блокировка является валидной с того момента, как ей удалось захватить ресурс
* boolean validate(stamp) - а является ли блокировка валидной?
* tryConvertToWriteLock() - иногда может быть полезным преобразовать блокировку для чтения в блокировку для записи не высвобождая ресурсы

Вот таким образом следовало бы переписать наш предыдущий пример под использование StampedLock:
```java 
ExecutorService executor = Executors.newFixedThreadPool(2);
Map<String, String> map = new HashMap<>();
StampedLock lock = new StampedLock();
 
executor.submit(() -> {
    long stamp = lock.writeLock();
    try {
        sleep(1);
        map.put("foo", "bar");
    } finally {
        lock.unlockWrite(stamp);
    }
});
 
Runnable readTask = () -> {
    long stamp = lock.readLock();
    try {
        System.out.println(map.get("foo"));
        sleep(1);
    } finally {
        lock.unlockRead(stamp);
    }
};
 
executor.submit(readTask);
executor.submit(readTask);
 
executor.shutdown();
```
Работать этот код будет точно так же, как и его брат-близнец с ReadWriteLock. Тут, правда, стоит упомянуть, что в StampedLock не реализована реентерантность. Поэтому особое внимание нужно уделять тому, чтобы не попасть в ситуацию взаимной блокировки (deadlock).

В следующем примере демонстрируется «оптимистичная блокировка»:
```java 
ExecutorService executor = Executors.newFixedThreadPool(2);
StampedLock lock = new StampedLock();
 
executor.submit(() -> {
    long stamp = lock.tryOptimisticRead();
    try {
        System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
        sleep(1);
        System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
        sleep(2);
        System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
    } finally {
        lock.unlock(stamp);
    }
});
 
executor.submit(() -> {
    long stamp = lock.writeLock();
    try {
        System.out.println("Write Lock acquired");
        sleep(2);
    } finally {
        lock.unlock(stamp);
        System.out.println("Write done");
    }
});
 
executor.shutdown();
```
Оптимистичная блокировка для чтения, вызываемая с помощью метода tryOptimisticRead(), отличается тем, что она всегда будет возвращать «штамп» не блокируя текущий поток, вне зависимости от того, занят ли ресурс, к которому она обратилась. В случае, если ресурс был заблокирован блокировкой для записи, возвращённый штамп будет равняться нулю. В любой момент можно проверить, является ли блокировка валидной с помощью lock.validate(stamp). Для приведённого выше кода результат будет таким:
```text 
Optimistic Lock Valid: true
Write Lock acquired
Optimistic Lock Valid: false
Write done
Optimistic Lock Valid: false
```
Оптимистичная блокировка является валидной с того момента, как ей удалось захватить ресурс. В отличии от обычных блокировок для чтения, оптимистичная не запрещает другим потокам блокировать ресурс для записи. Что же происходит в коде выше? После захвата ресурса блокировка является валидной и оптимистичный поток отправляется спать. В это время другой поток блокирует ресурсы для записи, не дожидаясь окончания работы чтения. Начиная с этого момента, оптимистичная блокировка перестаёт быть валидной (даже после окончания записи).

Таким образом, при использовании оптимистичных блокировок вам нужно постоянно следить за их валидностью (проверять её нужно уже после того, как выполнены все необходимые операции).

Иногда может быть полезным преобразовать блокировку для чтения в блокировку для записи не высвобождая ресурсы. В StampedLock это можно сделать с помощью метода tryConvertToWriteLock(), как в этом примере:
```java 
ExecutorService executor = Executors.newFixedThreadPool(2);
StampedLock lock = new StampedLock();
 
executor.submit(() -> {
    long stamp = lock.readLock();
    try {
        if (count == 0) {
            stamp = lock.tryConvertToWriteLock(stamp);
            if (stamp == 0L) {
                System.out.println("Could not convert to write lock");
                stamp = lock.writeLock();
            }
            count = 23;
        }
        System.out.println(count);
    } finally {
        lock.unlock(stamp);
    }
});
 
executor.shutdown();
```
В этом примере мы хотим прочитать значение переменной count и вывести его в консоль. Однако, если значение равно нулю, мы хотим изменить его на 23. Для этого нужно выполнить преобразования из readLock во writeLock, чтобы не помешать другим потокам обрабатывать переменную. В случае, если вы вызвали tryConvertToWriteLock() в тот момент, когда ресурс занят для записи другим потоком, текущий поток остановлен не будет, однако метод вернёт нулевое значение. В таком случае можно вызвать writeLock() вручную.





 
### Коллекции
Списки:
* CopyOnWriteArrayList (список только для частых чтений и редких обновлений)
* Vector (замена ArrayList)

Словарь:
* ConcurrentMap
* ConcurrentHashMap (основная многопоточная реализация HashMap)
* ConcurrentSkipListMap (отсортированная многопоточная реализация)
* Collections.synchronizedMap(Map<K,V> m) (если ну очень надо)

Набор:
* ConcurrentSkipListSet (отсортированный многопоточный набор)
* CopyOnWriteArraySet (набор только для частых чтений и редких обновлений)

Очереди:
* ArrayBlockingQueue (блокирующая очередь)
* ConcurrentLinkedDeque (стоит использовать только, если обязательно нужно LIFO, т.к. за счет двунаправленности нод данный класс проигрывает по производительности на 40% по сравнению с ConcurrentLinkedQueue)
* ConcurrentLinkedQueue (очередь на связанных нодах)
* DelayQueue (очередь с задержкой для каждого элемента)
* LinkedBlockingDeque (блокирующая двунаправленная очередь на связанных нодах)
* LinkedBlockingQueue (блокирующая очередь на связанных нодах)
* LinkedTransferQueue (может служить для передачи элементов)
* PriorityBlockingQueue (многопоточная PriorityQueue)
* SynchronousQueue (простая многопоточная очередь)


Далее конкретно...

#### CopyOnWriteArrayList (только для частых чтений и редких обновлений)
Синхронизирован, но не используем потому, что при изменении создает полную копию в памяти, что может привести к OutOfMemory. 
Или использовать только для чтения!


#### Vector (замена ArrayList)
Все методы Vector являются синхронизированными, и Vector явно предназначен для функционирования в многопоточных средах. Но существуют ограничения его безопасности потока, а именно существуют зависимости состояния между определенными парами методов. (Аналогично, итераторы, возвращаемые Vector.iterator(), выдадут исключение ConcurrentModificationException, если Vector модифицирован другим потоком во время итерации.)


#### Collections.synchronizedMap(Map<K,V> m)
Важно! Обращайтесь, если очень надо:
```java 
HashMap hm = new HashMap();
Map mm = Collections.synchronizedMap(hm); 
```

#### ConcurrentMap
Интерфейс ConcurrentMap наследуется от обычного Map и предоставляет описание одной из самой полезной коллекции для конкурентного использования. Чтобы продемонстрировать новые методы интерфейса, мы будем использовать вот эту заготовку:
```java 
ConcurrentMap<String, String> concurrentMap = new ConcurrentHashMap<>();
concurrentMap.put("foo", "bar");
concurrentMap.put("han", "solo");
concurrentMap.put("r2", "d2");
concurrentMap.put("c3", "p0");
```
Метод forEach() принимает лямбду типа BiConsumer. Этой лямбде будут передаваться в качестве аргументов все ключи и значения таблицы по очереди. Этот метод может использоваться как замена for-each циклам с итерацией по всем Entry. Итерация выполняется последовательно, в текущем потоке.
```java 
map.forEach((key, value) -> System.out.printf("%s = %s\n", key, value));
```
Метод putIfAbsent() помещает в таблицу значение, только если по данному ключу ещё нет другого значения. Этот метод является потокобезопасным (о крайней мере, в реализации ConcurrentHashMap), поэтому вам не нужно использовать synchronized, когда вы хотите использовать его в нескольких потоках (то же самое справедливо и для обычного put()):
```java 
String value = map.putIfAbsent("c3", "p1");
System.out.println(value);    // p0
```
Метод getOrDefault() работает так же, как и обычный get(), с той лишь разницей, что при отсутствии значения по данному ключу он вернёт значение по-умолчанию, передаваемое вторым аргументом:
```java 
String value = map.getOrDefault("hi", "there");
System.out.println(value);    // there
```
Метод replaceAll() принимает в качестве аргумента лямбда-выражение типа BiFunction. Этой лямбде по очереди передаются все комбинации ключ-значения из карты, а результат, который она возвращает, записывается соответствующему ключу в качестве значения:
```java 
map.replaceAll((key, value) -> "r2".equals(key) ? "d3" : value);
System.out.println(map.get("r2"));    // d3
```
Если же вам нужно изменить таким же образом только один ключ, это позволяет сделать метод compute():
```java 
map.compute("foo", (key, value) -> value + value);
System.out.println(map.get("foo"));   // barbar
```
Кроме обычного compute(), существуют так же методы computeIfAbsent() и computeIfPresent(). Они изменяют значение только если значение по данному ключу отсутствует (или присутствует, соответственно).

И, наконец, метод merge(), который может быть использован для объединения существующего ключа с новым значением. В качестве аргумента он принимает ключ, новое значение и лямбду, которая определяет, как новое значение должно быть объединено со старым:
```java 
map.merge("foo", "boo", (oldVal, newVal) -> newVal + " was " + oldVal);
System.out.println(map.get("foo"));   // boo was bar
```
#### ConcurrentHashMap (основная многопоточная реализация)
Данная коллекция оптимизирована для работы с многопоточностью. Для чтения и записи.

Кроме методов, которые описаны в ConcurrencyMap, в ConcurrentHashMap было добавлено и ещё несколько своих. Так же, как и параллельные stream’ы, эти методы используют специальный ForkJoinPool, доступный через ForkJoinPool.commonPool() в Java 8. Этот пул использует свои настройки для количества потоков, основанные на количестве ядер. У меня их 4, а значит использоваться будет три потока:
```java 
System.out.println(ForkJoinPool.getCommonPoolParallelism());  // 3
```
Это значение может быть специально изменено с помощью параметра JVM:
```java 
-Djava.util.concurrent.ForkJoinPool.common.parallelism=5
```
Мы рассмотрим три новых метода: forEach, search and reduce. У каждого из них есть первый аргумент, который называется parallelismThreshold, который определяет минимальное количество элементов в коллекции, при котором операция будет выполняться в нескольких потоках. Т.е. если в коллекции 499 элементов, а первый параметр выставлен равным пятистам, то операция будет выполняться в одном потоке последовательно. В наших примерах мы будем использовать первый параметр равным в единице, чтобы операции всегда выполнялись параллельно.

Для примеров ниже мы будем использовать всё ту же таблицу, что и выше (однако объявим её именем класса, а не интерфейса. чтобы нам были доступны все методы):
```java 
ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
map.put("foo", "bar");
map.put("han", "solo");
map.put("r2", "d2");
map.put("c3", "p0");
```
Работает метод ForEach так же, как и в ConcurrentMap. Для иллюстрации многопоточности мы будем выводить названия потоков (не забывайте, что их количество для меня ограничено тремя):
```java 
map.forEach(1, (key, value) ->
    System.out.printf("key: %s; value: %s; thread: %s\n",
        key, value, Thread.currentThread().getName()));
 
// key: r2; value: d2; thread: main
// key: foo; value: bar; thread: ForkJoinPool.commonPool-worker-1
// key: han; value: solo; thread: ForkJoinPool.commonPool-worker-2
// key: c3; value: p0; thread: main
```

Метод search() принимает лямбда-выражение типа BiFunction, в которую передаются все пары ключ-значение по очереди. Функция должна возвращать null, если необходимое вхождение найдено. В случае, если функция вернёт не null, дальнейший поиск будет остановлен. Не забывайте, что данные в хэш-таблице хранятся неупорядоченно. Если вы будете полагаться на порядок, в котором вы добавляли данные в неё, вы можете не получить ожидаемого результата. Если условиям поиска удовлетворяют несколько вхождений, результат точно предсказать нельзя.
```java 
String result = map.search(1, (key, value) -> {
    System.out.println(Thread.currentThread().getName());
    if ("foo".equals(key)) {
        return value;
    }
    return null;
});
System.out.println("Result: " + result);
 
// ForkJoinPool.commonPool-worker-2
// main
// ForkJoinPool.commonPool-worker-3
// Result: bar
```
Или вот другой пример, который полагается только на значения:
```java 
String result = map.searchValues(1, value -> {
    System.out.println(Thread.currentThread().getName());
    if (value.length() > 3) {
        return value;
    }
    return null;
});
 
System.out.println("Result: " + result);
 
// ForkJoinPool.commonPool-worker-2
// main
// main
// ForkJoinPool.commonPool-worker-1
// Result: solo
```

Метод reduce() вы могли уже встречать в Java 8 Streams. Он принимает две лямбды типа BiFunction. Первая функция преобразовывает пару ключ/значение в один объект (любого типа). Вторая функция совмещает все полученные значения в единый результат, игнорируя любые возможные null-значения.
```java 
String result = map.reduce(1,
    (key, value) -> {
        System.out.println("Transform: " + Thread.currentThread().getName());
        return key + "=" + value;
    },
    (s1, s2) -> {
        System.out.println("Reduce: " + Thread.currentThread().getName());
        return s1 + ", " + s2;
    });
 
System.out.println("Result: " + result);
 
// Transform: ForkJoinPool.commonPool-worker-2
// Transform: main
// Transform: ForkJoinPool.commonPool-worker-3
// Reduce: ForkJoinPool.commonPool-worker-3
// Transform: main
// Reduce: main
// Reduce: main
// Result: r2=d2, c3=p0, han=solo, foo=bar
```
#### ConcurrentSkipListMap (отсортированная многопоточная реализация)
Является аналогом TreeMap с поддержкой многопоточности. Данные также сортируются по ключу и гарантируется усредненная производительность log(N) для containsKey, get, put, remove и других похожих операций.

#### ConcurrentSkipListSet (отсортированный многопоточный набор)
Имплементация Set интерфейса, выполненная на основе ConcurrentSkipListMap.

#### CopyOnWriteArraySet (редкие обновления, частые чтения)
Имплементация интерфейса Set, использующая за основу CopyOnWriteArrayList. В отличии от CopyOnWriteArrayList, дополнительных методов нет.


#### ArrayBlockingQueue (блокирующая очередь)
Массив блокирующей очереди размеро. Можем из одного потока в другой поток перекинуть данные.
```java 
public static void main(String[] args) throws InterruptedException {
    ArrayBlockingQueue<String> stringArrayBlockingQueue = new ArrayBlockingQueue<String>(10);

    /* Добавление */
    stringArrayBlockingQueue.put("sss");   // Ждет освобождения свободных ячеек и кладет в очередь по принципу FIFO
    stringArrayBlockingQueue.offer("sdfsdfdsf"); // Не использовать! Если положить не удалось, то вернет false и данные пропадут, и продолжить дальше.
    stringArrayBlockingQueue.add("sdfsdfsdf");  // Не использовать! Кладет в очередь элемент не ожидая пустых ячеек, но может вызвать исключение, что коллекция переполнена.

    /* Получение */
    stringArrayBlockingQueue.take();  // берет из очереди по принципу FIFO
    stringArrayBlockingQueue.remove(); // берет из очереди по принципу FIFO и удалить его из коллекции
    stringArrayBlockingQueue.remove("sss"); // вернуть конкретно "sss"и удалить его из коллекции
    stringArrayBlockingQueue.poll(); // получить элемент если он там есть, иначе вернет null
}
```
Пример. Производитель-потребитель
```java 
public class Multithreading {
    static class Producer {
        private ArrayBlockingQueue<String> stringArrayBlockingQueue;

        Producer(ArrayBlockingQueue<String> stringArrayBlockingQueue) {
            this.stringArrayBlockingQueue = stringArrayBlockingQueue;
        }

        void put(String x) {
            try {
                stringArrayBlockingQueue.put(x);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Consumer {
        private ArrayBlockingQueue<String> stringArrayBlockingQueue;

        Consumer(ArrayBlockingQueue<String> stringArrayBlockingQueue) {
            this.stringArrayBlockingQueue = stringArrayBlockingQueue;
        }

        String get() {
            try {
                return stringArrayBlockingQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static void main(String[] args) {
        int capavity = 5;

        final ArrayBlockingQueue<String> stringArrayBlockingQueue = new ArrayBlockingQueue<String>(capavity);

        /* Поток производителей */
        new Thread(() -> {
            Producer producer = new Producer(stringArrayBlockingQueue);
            for (int i = 0; i < capavity; i++) {
                System.out.println("Производитель добавил: " + String.valueOf(i));
                producer.put(String.valueOf(i));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        /* Поток потребителей */
        new Thread(() -> {
            Consumer consumer = new Consumer(stringArrayBlockingQueue);
            for (int i = 0; i < capavity; i++) {
                System.out.println("Потребитель получил: " + consumer.get());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
```
```text 
_______________________________
/opt/jdk/bin/java...
Производитель добавил: 0
Потребитель получил: 0
Производитель добавил: 1
Производитель добавил: 2
Производитель добавил: 3
Производитель добавил: 4
Потребитель получил: 1
Потребитель получил: 2
Потребитель получил: 3
Потребитель получил: 4

Process finished with exit code 0
```


#### ConcurrentLinkedDeque (очередь на связанных нодах)
Данные можно добавлять и вытаскивать с обоих сторон. Соответственно, класс поддерживает оба режима работы: FIFO (First In First Out) и LIFO (Last In First Out). На практике, ConcurrentLinkedDeque стоит использовать только, если обязательно нужно LIFO, т.к. за счет двунаправленности нод данный класс проигрывает по производительности на 40% по сравнению с ConcurrentLinkedQueue.

#### ConcurrentLinkedQueue (очередь на связанных нодах)
В имплементации используется wait-free алгоритм от Michael & Scott, адаптированный для работы с garbage collector'ом. Этот алгоритм довольно эффективен и, что самое важное, очень быстр, т.к. построен на CAS http://en.wikipedia.org/wiki/Compare-and-swap. Метод size() может работать долго, т.ч. лучше постоянно его не дергать. Детальное описание алгоритма можно посмотреть тут http://www.cs.rochester.edu/u/michael/PODC96.html.

#### DelayQueue (очередь с задержкой для каждого элемента)
Довольно специфичный класс, который позволяет вытаскивать элементы из очереди только по прошествии некоторой задержки, определенной в каждом элементе через метод getDelay интерфейса Delayed.

#### LinkedBlockingDeque (блокирующая очередь на связанных нодах)
Двунаправленная блокирующая очередь на связанных нодах, реализованная как простой двунаправленный список с одним локом. Размер очереди задается через конструктор и по умолчанию равен Integer.MAX_VALUE.

#### LinkedBlockingQueue (блокирующая очередь на связанных нодах)
Блокирующая очередь на связанных нодах, реализованная на «two lock queue» алгоритме: один лок на добавление, другой на вытаскивание элемента. За счет двух локов, по сравнению с ArrayBlockingQueue, данный класс показывает более высокую производительность, но и расход памяти у него выше. Размер очереди задается через конструктор и по умолчанию равен Integer.MAX_VALUE.

#### LinkedTransferQueue (может служить для передачи элементов)
Реализация TransferQueue на основе алгоритма Dual Queues with Slack. Активно использует CAS и парковку потоков, когда они находятся в режиме ожидания.

#### PriorityBlockingQueue (многопоточная PriorityQueue)
Является многопоточной оберткой над PriorityQueue. При вставлении элемента в очередь, его порядок определяется в соответствии с логикой Comparator'а или имплементации Comparable интерфейса у элементов. Первым из очереди выходит самый наименьший элемент.

#### SynchronousQueue (простая многопоточная очередь)
Эта очередь работает по принципу один вошел, один вышел. Каждая операция вставки блокирует «Producer» поток до тех пор, пока «Consumer» поток не вытащит элемент из очереди и наоборот, «Consumer» будет ждать пока «Producer» не вставит элемент.

# Советы 
1. Используйте локальные переменные
2. Предпочитайте неизменяемые классы изменяемым (String, Integer  и т.п.)
3. Сокращайте области синхронизации
4. Используйте пул потоков (Executor framework)
5. Используйте утилиты синхронизации (Semaphore, CountDownLatch, CyclicBarrier и т.п.) вместо wait() и notify()
6. Используйте BlockingQueue для реализации Producer-Consumer
7. Используйте потокобезопасные коллекции (Collections.synchronizedCollection и пр.) вместо коллекций с блокированием доступа - используйте CopyOnWriteArrayList вместо Collections.synchronizedList, если чтение из списка происходит чаще, чем его изменение.
8. Используйте семафоры для создания ограничений
9. Используйте блоки синхронизации вместо блокированных методов
10. Избегайте использования статических переменных
11. Используйте Lock вместо synchronized

# Полезные ссылки
- 10 советов по использованию ExecutorService https://habrahabr.ru/post/260953/

