package ru.davidlevi.conspects.exceptions;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainClass {
    public static void main(String[] args) {
        try (TestInput testInput = new TestInput()) {
            testInput.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class TestInput implements AutoCloseable {
    TestInput() throws FileNotFoundException {
        System.out.println("open");
    }

    public void read() throws IOException {
        System.out.println("read");
    }

    @Override
    public void close() throws Exception {
        System.out.println("close");
    }
}