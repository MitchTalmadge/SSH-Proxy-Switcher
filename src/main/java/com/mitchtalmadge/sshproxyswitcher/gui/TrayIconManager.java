package com.mitchtalmadge.sshproxyswitcher.gui;

import com.mitchtalmadge.sshproxyswitcher.SSHProxySwitcher;
import com.mitchtalmadge.sshproxyswitcher.Versioning;
import com.mitchtalmadge.sshproxyswitcher.managers.profiles.Profile;
import com.mitchtalmadge.sshproxyswitcher.managers.profiles.ProfileManager;
import com.mitchtalmadge.sshproxyswitcher.managers.proxies.ProxySettingsException;
import com.mitchtalmadge.sshproxyswitcher.managers.ssh.SSHConnectionException;
import javafx.application.Platform;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.logging.Level;

public class TrayIconManager implements ActionListener, ProfileManager.LoadedProfilesListener {

    private TrayIcon trayIcon;

    private MenuItem openItem;
    private MenuItem disconnectItem;
    private MenuItem exitItem;

    public TrayIconManager() {
        setupTrayIcon();
    }

    private void setupTrayIcon() {
        if (!SystemTray.isSupported()) {
            GUIHelper.showErrorDialog("Error", "Tray Icon Not Supported", "SSH Proxy Switcher does not work on systems that do not support tray icons. The program will now close.");
        } else {
            SystemTray tray = SystemTray.getSystemTray();

            PopupMenu popupMenu = buildPopupMenu(SSHProxySwitcher.getInstance().getProfileManager().getLoadedProfiles());

            trayIcon = new TrayIcon(Versioning.getTrayIconImage(TrayIconImage.DEFAULT), "SSH Proxy Switcher", popupMenu);

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

            SSHProxySwitcher.getInstance().getProfileManager().addLoadedProfilesListener(this);
        }
    }

    private PopupMenu buildPopupMenu(ArrayList<Profile> profiles) {
        PopupMenu popupMenu = new PopupMenu();

        openItem = new MenuItem("Open Control Panel");
        openItem.addActionListener(this);
        popupMenu.add(openItem);

        popupMenu.addSeparator();

        disconnectItem = new MenuItem("Disconnect");
        disconnectItem.addActionListener(this);
        popupMenu.add(disconnectItem);

        if (profiles != null) {
            popupMenu.addSeparator();

            for (Profile profile : profiles) {
                ProfileMenuItem menuItem = new ProfileMenuItem("Use " + profile.getProfileName(), profile);
                menuItem.addActionListener(this);
                popupMenu.add(menuItem);
            }
        }

        popupMenu.addSeparator();

        exitItem = new MenuItem("Exit SSH Proxy Switcher");
        exitItem.addActionListener(this);
        popupMenu.add(exitItem);

        return popupMenu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(openItem)) {
            SSHProxySwitcher.getInstance().showMainWindow();
        } else if (e.getSource().equals(disconnectItem)) {
            SSHProxySwitcher.getInstance().getSSHManager().stopConnection();
            SSHProxySwitcher.getInstance().getProxyManager().disableProxySettings();
        } else if (e.getSource() instanceof ProfileMenuItem) {
            Profile profile = ((ProfileMenuItem) e.getSource()).getOwningProfile();
            if (profile.shouldConnectToSsh()) {
                try {
                    SSHProxySwitcher.getInstance().getSSHManager().startConnection(profile);
                } catch (SSHConnectionException e1) {
                    e1.printStackTrace();
                }
            }

            if (profile.shouldAutoEnableProxy()) {
                try {
                    SSHProxySwitcher.getInstance().getProxyManager().setProxySettings(profile);
                } catch (ProxySettingsException e1) {
                    e1.printStackTrace();
                }
            }
        } else if (e.getSource().equals(exitItem)) {
            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Exiting SSH Proxy Switcher");
            Platform.exit();
        } else if (e.getSource().equals(trayIcon)) { //Double Click Icon
            SSHProxySwitcher.getInstance().showMainWindow();
        }
    }

    @Override
    public void loadedProfilesUpdated(ArrayList<Profile> loadedProfiles) {
        trayIcon.setPopupMenu(buildPopupMenu(loadedProfiles));
    }

    private class ProfileMenuItem extends MenuItem {
        private Profile owningProfile;

        public ProfileMenuItem(String label, Profile owningProfile) {
            super(label);

            this.owningProfile = owningProfile;
        }

        public Profile getOwningProfile() {
            return owningProfile;
        }

        public void setOwningProfile(Profile owningProfile) {
            this.owningProfile = owningProfile;
        }
    }
}
