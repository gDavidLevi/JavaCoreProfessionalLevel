package ru.davidlevy.conspects.swing;

import javax.swing.*;

public class MainClass2 {
    private static void window() {
        JFrame jFrame = new JFrame("Заголовок окна");
        jFrame.setBounds(200, 200, 400, 300);
        //
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.add(new JLabel("Привет, мир!"));
        //
        jFrame.setVisible(true);
    }

    public static void main(String[] args) {
         /*
         Swing имеет собственный управляющий поток (т.н. dispatching thread),
         который работает параллельно с основным (стартовым, в котором выполняется main())
         потоком. Это означает что если основной поток закончит работу (метод main завершится),
         поток отвечающий за работу Swing-интерфейса может продолжать свою работу.
         И даже если пользователь закрыл все окна, программа продолжит свою работу
         (до тех пор, пока жив данный поток).

         Запускаем весь код, работающий с интерфейсом, в управляющем потоке, даже инициализацию:
         */
        //javax.swing.SwingUtilities.invokeLater(() -> window());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                window();
            }
        });
    }
}