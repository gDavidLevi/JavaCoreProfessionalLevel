package ru.davidlevi.conspects.enumeration;

import java.util.Arrays;

public class MainClass {
    private enum MyType {
        ONE, TWO, THREE
    }

    public static void main(String[] args) {
        MyType myType;
        myType = MyType.ONE;
        //
        if (myType != MyType.THREE) System.out.println("не равны"); // не равны
        //
        switch (myType) {
            case ONE:
                break;
            case TWO:
                break;
            case THREE:
                break;
            default:
                throw new RuntimeException();
        }
        //
        myType = MyType.valueOf("TWO");
        System.out.println(myType); // TWO
        //
        MyType[] array = MyType.values();
        System.out.println(Arrays.toString(array)); // [ONE, TWO, THREE]
        //
        MyType elem = MyType.values()[0];
        System.out.println(elem); // ONE
    }
}