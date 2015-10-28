package net.liveforcode.SSHProxySwitcher.GUI;

import javax.swing.*;
import java.awt.*;

public class ProfileConfiguration extends JPanel {
    private JPanel contentPane;
    private JTextField profileNameField;
    private JPanel sshSettingsPanel;
    private JTextField sshHostNameField;
    private JTextField sshPortField;
    private JTextField sshProxyPortField;
    private JList savedSshSettingsList;
    private JButton loadFromSelectedButton;
    private JButton saveAsSpecifiedButton;
    private JTextField sshUsernameField;
    private JPanel savedSshSettingsPanel;
    private JTextField sshPrivateKeyField;
    private JPasswordField sshPasswordField;
    private JButton browseButton;
    private JPanel formPanel;
    private JPanel footerPanel;
    private JButton deleteProfileButton;
    private JTextField textField2;
    private JButton saveProfileButton;
    private JPanel headerPanel;

    public ProfileConfiguration()
    {
        setLayout(new BorderLayout());
        add(this.contentPane, BorderLayout.CENTER);
    }

}
