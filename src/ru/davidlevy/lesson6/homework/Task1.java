package ru.davidlevy.lesson6.homework;

import static java.lang.System.arraycopy;

class Task1 {
    /**
     * Метод просматривает линейный массив с конца и ищет 4-ку. Если он встречает искомое значение, и оно последнее или
     * предпоследнее, то результат будет [0, 0], иначе метод вернет новый массив из 2-х элементов, в который запишутся
     * последующие два элемента от 4-ки.
     * Метод бросает исключение класса RuntimeException, если не нашел во входном массиве 4-ку.
     *
     * @param source входной массив типа int
     * @return выходной массив типа int
     */
    int[] methodFour(int[] source) {
        int[] receiver = null;
        int maxIndex = source.length - 1;

        /* Если с массиве 4ка на последнем месте */
        if (source[maxIndex] == 4)
            return new int[0];

        /* В остальных случаях */
        for (int i = maxIndex; i >= 0; i--) {
            if (source[i] == 4 & i <= maxIndex - 1) {
                receiver = new int[i == maxIndex - 1 ? 1 : 2];
                arraycopy(source, i + 1, receiver, 0, receiver.length);
                return receiver;
            }
        }
        /* В массиве нет 4-ок*/
        throw new RuntimeException();
    }
}