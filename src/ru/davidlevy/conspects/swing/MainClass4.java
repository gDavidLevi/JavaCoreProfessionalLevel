package ru.davidlevy.conspects.swing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainClass4 {
    public static void main(String[] args) {
        new Listener();
    }
}

class Listener extends JFrame {
    public Listener() throws HeadlessException {
        setTitle("Listener");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(300, 300, 400, 300);
        //
        JButton jButton = new JButton("Click me");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("jButton.addAction");
            }
        });
        //
        JLabel jLabel = new JLabel();
        jLabel.setText("Value: ");
        //
        JSlider jSlider = new JSlider();
        jSlider.setMaximum(100);
        jSlider.setMinimum(0);
        jSlider.setValue(45);
        jSlider.setOrientation(JSlider.HORIZONTAL);
        jSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                jLabel.setText("Value: " + jSlider.getValue());
            }
        });
        //
        setLayout(new GridLayout(10,1));
        add(jButton);
        add(jLabel);
        add(jSlider);
        //
        setVisible(true);
    }
}

