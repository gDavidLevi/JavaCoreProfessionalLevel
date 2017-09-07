package ru.davidlevy.conspects.swing;

import javax.swing.*;
import java.awt.*;

public class MainClass3 {
    public static void main(String[] args) {
        new Layout();
    }
}

class Layout extends JFrame {
    /*
    Структура фрейма: панель.методы
    Структура фрейма: панель | слой_или_компоновщик | виджеты
    Структура фрейма: панель | виджеты
     */
    Layout() throws HeadlessException {
        // Описание свойств окна
        setTitle("Layouts");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(400, 300);
        setInCenterDesktop();
        // Добавление виджетов
        //add(new JLabel("Привет, мир!"));

        // Добавление объекта класса JPanel
        // BorderLayout (о сторонам света)
//        JPanel jPanel = new JPanel(new BorderLayout());
//        jPanel.add(new Button("NORTH"), BorderLayout.NORTH);
//        jPanel.add(new Button("CENTER"), BorderLayout.CENTER);
//        jPanel.add(new Button("SOUTH"), BorderLayout.SOUTH);
//        jPanel.add(new Button("EAST"), BorderLayout.EAST);
//        jPanel.add(new Button("WEST"), BorderLayout.WEST);
//        setContentPane(jPanel);

        // BoxLayout (в строку или в столбец)
//        JButton[] jButtons = new JButton[4];
//        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS)); // BoxLayout.X_AXIS
//        for (JButton button : jButtons) {
//            button = new JButton("кнопка");
//            button.setAlignmentX(CENTER_ALIGNMENT);
//            add(button);
//        }

        // FlowLayout (в строку с переносом на новую строчку с выравниманием по центру)
//        JButton[] jButtons = new JButton[17];
//        setLayout(new FlowLayout());
//        for (JButton button : jButtons) {
//            button = new JButton("кнопка");
//            add(button);
//        }

        // GridLayout (в таблицу с переносом на новую строку с выравниванием по левому краю)
        JButton[] jButtons = new JButton[17];
        setLayout(new GridLayout(4, 3));
        for (JButton button : jButtons) {
            button = new JButton("кнопка");
            add(button);
        }

        // Расстановка в ручную элементов
        // следует сделать setLayout(null);

        // Отображение
        setVisible(true);
    }

    private void setInCenterDesktop() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);
    }
}