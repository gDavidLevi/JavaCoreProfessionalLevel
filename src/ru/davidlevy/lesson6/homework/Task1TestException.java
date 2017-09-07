package ru.davidlevy.lesson6.homework;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.davidlevy.lesson6.homework.Task1;

public class Task1TestException {
    private Task1 task1;

    @Before
    public void startTest() {
        task1 = new Task1();
    }

    @Test(expected = RuntimeException.class)
    public void testException() {
        Assert.assertArrayEquals(task1.methodFour(new int[]{1, 2, 3, 7, 2, 3, 0, 1, 7}), null);
    }
}