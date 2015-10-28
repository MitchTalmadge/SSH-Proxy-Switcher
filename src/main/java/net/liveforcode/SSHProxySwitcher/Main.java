package net.liveforcode.SSHProxySwitcher;

import net.liveforcode.SSHProxySwitcher.GUI.MainInterface;

import javax.swing.*;

public class Main {

    public static void main(String... args) {

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainInterface().setVisible(true);
            }
        });

    }

}
