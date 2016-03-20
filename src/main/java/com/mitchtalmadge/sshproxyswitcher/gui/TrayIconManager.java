package com.mitchtalmadge.sshproxyswitcher.gui;

import com.mitchtalmadge.sshproxyswitcher.SSHProxySwitcher;
import com.mitchtalmadge.sshproxyswitcher.Versioning;
import com.mitchtalmadge.sshproxyswitcher.managers.profiles.Profile;
import com.mitchtalmadge.sshproxyswitcher.managers.profiles.ProfileManager;
import javafx.application.Platform;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.logging.Level;

public class TrayIconManager implements ActionListener, ProfileManager.LoadedProfilesListener {

    public static final int STATUS_DEFAULT = 0;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_CONNECTED = 2;
    public static final int STATUS_ERROR = 3;

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

        Profile connectedProfile = SSHProxySwitcher.getInstance().getProfileManager().getConnectedProfile();

        disconnectItem = new MenuItem("Disconnect");
        disconnectItem.addActionListener(this);
        disconnectItem.setEnabled(connectedProfile != null);
        popupMenu.add(disconnectItem);

        if (profiles != null) {
            popupMenu.addSeparator();

            for (Profile profile : profiles) {
                String itemValue = "";
                if (connectedProfile != null && connectedProfile.getProfileName().equals(profile.getProfileName()))
                    itemValue = "✓ " + profile.getProfileName();
                else
                    itemValue = profile.getProfileName();
                ProfileMenuItem menuItem = new ProfileMenuItem(itemValue, profile);
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

    private void refreshPopupMenu() {
        trayIcon.setPopupMenu(buildPopupMenu(SSHProxySwitcher.getInstance().getProfileManager().getLoadedProfiles()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(openItem)) {
            SSHProxySwitcher.getInstance().showMainWindow();
        } else if (e.getSource().equals(disconnectItem)) {
            SSHProxySwitcher.getInstance().getProfileManager().disconnectProfiles();
            refreshPopupMenu();
        } else if (e.getSource() instanceof ProfileMenuItem) {
            Profile profile = ((ProfileMenuItem) e.getSource()).getOwningProfile();
            SSHProxySwitcher.getInstance().getProfileManager().connectProfile(profile);
            refreshPopupMenu();
        } else if (e.getSource().equals(exitItem)) {
            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Exiting SSH Proxy Switcher");
            Platform.exit();
        } else if (e.getSource().equals(trayIcon)) { //Double Click Icon
            SSHProxySwitcher.getInstance().showMainWindow();
        }
    }

    public void setStatus(int status) {
        switch (status) {
            default:
            case STATUS_DEFAULT:
                trayIcon.setImage(Versioning.getTrayIconImage(TrayIconImage.DEFAULT));
                break;
            case STATUS_CONNECTING:
                trayIcon.setImage(Versioning.getTrayIconImage(TrayIconImage.YELLOW));
                break;
            case STATUS_CONNECTED:
                trayIcon.setImage(Versioning.getTrayIconImage(TrayIconImage.GREEN));
                break;
            case STATUS_ERROR:
                trayIcon.setImage(Versioning.getTrayIconImage(TrayIconImage.RED));
                break;
        }
    }

    @Override
    public void loadedProfilesUpdated(ArrayList<Profile> loadedProfiles) {
        refreshPopupMenu();
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
