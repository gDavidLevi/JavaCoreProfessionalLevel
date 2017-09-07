package ru.davidlevy.conspects.junit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class ClassForTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data53434() {
        /* имя коллекции вместо data53434 можно указать любое */
        return Arrays.asList(
                new Object[][] {
                        {0,0,0},
                        {1,1,2},
                        {2,2,4},
                        {2,5,10}, // 7
                        {4,2,6},
                        {1,3,4},
                        {6,-2,4},
                        {-1,5,4}
                }
        );
    }

    private int a;
    private int b;
    private int c;

    /* порядковая ассоциация с коллекцией */
    public ClassForTest(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    private Calculator calculator;

    @Before
    public void init() {
        calculator = new Calculator();
    }

    @Test
    public void massTestAdd() {
        /* Тест в assert будет подставлять значения a,b,c по порядку из коллекции */
        Assert.assertEquals(c, calculator.add(a, b));
    }
}

