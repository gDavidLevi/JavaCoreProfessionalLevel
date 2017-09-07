package ru.davidlevy.lesson7.classwork;

public class Unknown {
    int x;
    int y;

    public Unknown(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void doSomething() {
        System.out.println("Unknown class method doSomething (" + x + " " + y + ")");
    }
}