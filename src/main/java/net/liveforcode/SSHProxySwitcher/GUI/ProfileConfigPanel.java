package net.liveforcode.SSHProxySwitcher.GUI;

import javax.swing.*;
import java.awt.*;

public class ProfileConfigPanel extends JPanel {
    private JPanel contentPane;
    private JPanel headerPanel;
    private JTextField profileNameField;
    private JPanel footerPanel;
    private JButton deleteProfileButton;
    private JButton saveProfileButton;
    private JPanel sshSettingsPanel;
    private JTextField sshHostNameField;
    private JTextField sshPortField;
    private JTextField sshProxyPortField;
    private JTextField sshUsernameField;
    private JPasswordField sshPasswordField;
    private JTextField sshPrivateKeyField;
    private JButton browseButton;

    public ProfileConfigPanel() {
        setLayout(new BorderLayout());
        add(this.contentPane, BorderLayout.CENTER);
    }
}
