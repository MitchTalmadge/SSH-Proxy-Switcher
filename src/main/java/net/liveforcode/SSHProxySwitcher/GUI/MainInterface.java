package net.liveforcode.SSHProxySwitcher.GUI;

import net.liveforcode.SSHProxySwitcher.SSHProxySwitcher;
import net.liveforcode.SSHProxySwitcher.Versioning;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainInterface extends JFrame implements MouseListener {

    private JPanel contentPane;
    private JLabel titleLabel;
    private JPanel headerPanel;
    private JPanel configPanel;
    private JPanel profilesPanel;
    JTree profilesTree;
    private JButton newProfileButton;
    private JLabel copyrightLabel;
    private JLabel donateLabel;
    private SSHProxySwitcher sshProxySwitcher;

    public MainInterface(SSHProxySwitcher sshProxySwitcher) {
        this.sshProxySwitcher = sshProxySwitcher;

        setTitle("SSH Proxy Switcher");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //TODO: Tray icon
        setContentPane(this.contentPane);

        this.titleLabel.setText(Versioning.PROGRAM_NAME_WITH_VERSION);
        this.copyrightLabel.addMouseListener(this);
        this.copyrightLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        this.donateLabel.addMouseListener(this);
        this.donateLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        this.configPanel.add(new ProfileConfigPanel(), BorderLayout.CENTER);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getSource().equals(this.donateLabel))
            try {
                Desktop.getDesktop().browse(new URI("https://donate.liveforcode.net"));
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
        else if (e.getSource().equals(this.copyrightLabel)) {
            try {
                Desktop.getDesktop().browse(new URI("https://liveforcode.net"));
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
