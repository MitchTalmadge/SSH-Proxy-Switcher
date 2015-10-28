package net.liveforcode.SSHProxySwitcher.GUI;

import net.liveforcode.SSHProxySwitcher.Versioning;
import sun.misc.Version;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class MainInterface extends JFrame {

    private JPanel contentPane;
    private JLabel titleLabel;
    private JPanel headerPanel;
    private JPanel configPanel;
    private JPanel profilesPanel;
    private JTree profilesTree;
    private JButton newProfileButton;
    private JLabel copyrightLabel;
    private JLabel donateLabel;

    public MainInterface() {
        setTitle("SSH Proxy Switcher");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //TODO: Tray icon

        this.titleLabel.setText(Versioning.PROGRAM_NAME_WITH_VERSION);

        setContentPane(this.contentPane);

        this.configPanel.add(new ProfileConfigPanel(), BorderLayout.CENTER);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

}
