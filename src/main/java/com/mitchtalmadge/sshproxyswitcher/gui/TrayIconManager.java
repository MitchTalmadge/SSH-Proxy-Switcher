package com.mitchtalmadge.sshproxyswitcher.gui;

import com.mitchtalmadge.sshproxyswitcher.SSHProxySwitcher;
import com.mitchtalmadge.sshproxyswitcher.Versioning;
import javafx.application.Platform;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;

public class TrayIconManager implements ActionListener {

    private SSHProxySwitcher proxySwitcher;
    private TrayIcon trayIcon;

    private PopupMenu popupMenu;
    private MenuItem openItem;
    private MenuItem exitItem;

    public TrayIconManager(SSHProxySwitcher proxySwitcher) {
        this.proxySwitcher = proxySwitcher;
        setupTrayIcon();
    }

    private void setupTrayIcon() {
        if (!SystemTray.isSupported()) {
            GUIHelper.showErrorDialog("Error", "Tray Icon Not Supported", "SSH Proxy Switcher does not work on systems that do not support tray icons. The program will now close.");
        } else {
            SystemTray tray = SystemTray.getSystemTray();

            popupMenu = new PopupMenu();

            openItem = new MenuItem("Open Control Panel");
            openItem.addActionListener(this);
            popupMenu.add(openItem);

            popupMenu.addSeparator();

            exitItem = new MenuItem("Exit SSH Proxy Switcher");
            exitItem.addActionListener(this);
            popupMenu.add(exitItem);

            trayIcon = new TrayIcon(Versioning.getTrayIconImage(TrayIconImage.DEFAULT), "SSH Proxy Switcher", popupMenu);
            trayIcon.setPopupMenu(popupMenu);

            trayIcon.addActionListener(this);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                }
            });

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(openItem)) {
            proxySwitcher.showMainWindow();
        } else if (e.getSource().equals(exitItem)) {
            proxySwitcher.getLoggingManager().log(Level.INFO, "Exiting SSH Proxy Switcher");
            Platform.exit();
        } else if (e.getSource().equals(trayIcon)) { //Double Click Icon
            proxySwitcher.showMainWindow();
        }
    }
}
