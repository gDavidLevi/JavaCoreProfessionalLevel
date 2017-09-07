package ru.davidlevy.conspects.lambda;

public class InterfaceTwoMethods {
    public static void main(String[] args) {
        GameObject gameObject; // реализует "множественное наследование": и new One() и new Two()
        gameObject = new One();
        gameObject.update();
        gameObject.draw();
        gameObject = new Two();
        gameObject.update();
        gameObject.draw();
        // Поскольку InterfaceB это расширение интерфейса Figure, то
        // используя приведение типов мы можем получить доступ к методам
        // из интерфейса InterfaceB.
        //((InterfaceB)gameObject).methodB();
    }
}

interface GameObject { // интерфейс
    void update();
    void draw();
}

interface InterfaceB extends GameObject { // InterfaceB расширяет интерфейс интерфейсом Figure
    void methodB();
}

class One implements GameObject{ // реализует все методы интерфейса Figure
    @Override
    public void update() {
    }

    @Override
    public void draw() {
    }
}

class Two implements GameObject { // реализует все методы интерфейса Figure
    @Override
    public void update() {
    }

    @Override
    public void draw() {
    }
}

abstract class Three implements InterfaceB {
    // данный класс абстрактный и он может не реализовывать методы объявленные в интерфейсе InterfaceB.
}

class Four extends Three {  // унаследовали от абстрактного класса и реализует все унаследованные методы
    @Override
    public void update() {
    }

    @Override
    public void draw() {
    }

    @Override
    public void methodB() {
    }
}


class Five implements InterfaceB { // реализует все методы интерфейса InterfaceB
    @Override
    public void update() {
    }

    @Override
    public void draw() {
    }

    @Override
    public void methodB() {
    }
}