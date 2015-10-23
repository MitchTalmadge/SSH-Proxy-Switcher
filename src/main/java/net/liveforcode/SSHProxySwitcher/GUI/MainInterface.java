package net.liveforcode.SSHProxySwitcher.GUI;

import javax.swing.*;
import java.awt.*;

public class MainInterface extends JFrame {

    private JPanel contentPane;
    private JLabel titleLabel;
    private JPanel headerPanel;
    private JPanel centerPanel;
    private JPanel treePanel;
    private JTree tree;

    public MainInterface()
    {
        setTitle("SSH Proxy Switcher");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //TODO: Tray icon

        setPreferredSize(new Dimension(600,400));
        setMinimumSize(getPreferredSize());
        setResizable(false);

        setContentPane(this.contentPane);
        pack();
        setLocationRelativeTo(null);
    }

}
