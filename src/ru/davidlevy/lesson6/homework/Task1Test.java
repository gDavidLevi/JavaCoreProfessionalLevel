package ru.davidlevy.lesson6.homework;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class Task1Test {
    @Parameterized.Parameters
    public static Collection<Object[]> collection() {
        return Arrays.asList(
                new Object[][]{
                        {new int[]{4, 9, 8, 7, 6, 5}, new int[]{9, 8}},
                        {new int[]{0, 4, 1, 7, 1, 7}, new int[]{1, 7}},
                        {new int[]{0, 0, 4, 5, 6, 0}, new int[]{5, 6}},
                        {new int[]{0, 0, 0, 4, 7, 9}, new int[]{7, 9}},
                        {new int[]{0, 0, 0, 0, 4, 3}, new int[]{3}},
                        {new int[]{0, 0, 0, 0, 0, 4}, new int[]{}}
                }
        );
    }

    private int[] datas;
    private int[] result;

    public Task1Test(int[] datas, int[] results) {
        this.datas = datas;
        this.result = results;
    }

    private Task1 task1;

    @Before
    public void startTest() {
        task1 = new Task1();
    }

    @Test
    public void testWork() {
        Assert.assertArrayEquals(task1.methodFour(datas), result);
    }
}