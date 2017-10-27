package ru.davidlevi.lesson6.homework;

class Task2 {
    boolean methodOneAndFour(int[] array) {
        boolean oneIsPresent = false;
        boolean fourIsPresent = false;
        for (int element : array) {
            if (element != 1 && element != 4)
                oneIsPresent = false;
            if (element == 1)
                oneIsPresent = true;
            if (element == 4)
                fourIsPresent = true;
        }
        return oneIsPresent && fourIsPresent;
    }
}