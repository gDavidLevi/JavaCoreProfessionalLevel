package ru.davidlevy.lesson6.homework;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class Task2Test {
    @Parameterized.Parameters
    public static Collection<Object[]> collection() {
        return Arrays.asList(
                new Object[][]{
                        {new int[]{4, 4, 4, 4, 4, 1, 1, 1, 1, 1}, true},
                        {new int[]{1, 1, 1, 1, 1, 4, 4, 4, 4, 4}, true},
                        {new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, false},
                        {new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4}, false},
                        {new int[]{4, 4, 4, 4, 4, 1, 1, 1, 8, 8}, false}
                }
        );
    }

    private int[] datas;
    private boolean isPresent;

    public Task2Test(int[] datas, boolean result) {
        this.datas = datas;
        this.isPresent = result;
    }

    private Task2 task2;

    @Before
    public void startTest() {
        task2 = new Task2();
    }

    @Test
    public void testWork() {
        Assert.assertEquals(task2.methodOneAndFour(datas), isPresent);
    }
}