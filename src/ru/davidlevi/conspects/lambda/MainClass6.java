package ru.davidlevi.conspects.lambda;

public class MainClass6 {
}

// Класс MyServer создан для регистрации событий (eventListener) возникших в MyServer, перечисленных
// в интерфейсе MyListener, реализованных в MyServerGUI.
class MyServer {
    private final MyListener eventListener;

    // Конструктор должен инициироваться интерфейсом для того, чтобы была привязка к интерфейсу.
    MyServer(MyListener event) {
        this.eventListener = event;
    }

    public void putLog(String message) {
        // возникло событие в этом классе (this) и  мы передаем сообщение
        eventListener.onLog(this, message);
    }
}

// Мы создали данный интерфейс для разделения классов MyServer и MyServerGUI, логики от интерфейса
interface MyListener {
    void onLog(MyServer myServer, String message);
}

// Мы пишем лог в окне MyServerGUI, поэтому метод onLog интерфейса MyListener реализован именно здесь
class MyServerGUI implements MyListener {
    private StringBuilder log = new StringBuilder();

    // реализация метода onLog
    @Override
    public void onLog(MyServer myServer, String message) {
        log.append(message);
    }
}