package ru.davidlevi.conspects.lambda;

public class MouseListener1 {
    public static void main(String[] args) {
        OnMouseListener listener = new MouseAdapter(){ // вызов из ананимного класса нужных нам методов
            @Override
            public void button() {
                super.button();
                System.out.println(" @Override class MouseAdapter void button()");
            }

            @Override
            public void move() {
                super.move();
                System.out.println(" @Override class MouseAdapter void move()");
            }
        };
        listener.button();
        listener.well();
        listener.move();

    }
}

interface OnMouseListener{
    void button();
    void well();
    void move();
}

class MouseAdapter implements OnMouseListener { // реализация методов для вызова в анонимном классе

    @Override
    public void button() {
        System.out.println("class MouseAdapter void button()");
    }

    @Override
    public void well() {
        System.out.println("class MouseAdapter void well()");
    }

    @Override
    public void move() {
        System.out.println("class MouseAdapter void move()");
    }
}