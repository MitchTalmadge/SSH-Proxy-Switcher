package com.mitchtalmadge.sshproxyswitcher.managers.profiles;

import com.mitchtalmadge.sshproxyswitcher.SSHProxySwitcher;
import com.mitchtalmadge.sshproxyswitcher.Versioning;
import com.mitchtalmadge.sshproxyswitcher.gui.TrayIconManager;
import com.mitchtalmadge.sshproxyswitcher.managers.properties.PropertiesEnum;
import com.mitchtalmadge.sshproxyswitcher.managers.proxies.ProxySettingsException;
import com.mitchtalmadge.sshproxyswitcher.managers.ssh.SSHConnectionException;
import javafx.application.Platform;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

public class ProfileManager {

    protected static final File profilesFile = new File(Versioning.getApplicationDir() + "/profiles.ser");
    protected ArrayList<Profile> loadedProfiles;
    protected Profile connectedProfile;

    protected ArrayList<LoadedProfilesListener> listeners = new ArrayList<>();

    public void saveProfiles() {
        if (loadedProfiles != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(profilesFile);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

                int profilesCount = loadedProfiles.size(); //Used to specify how many profiles are saved, and how many should be loaded.
                objectOutputStream.writeInt(profilesCount);

                for (Profile profile : loadedProfiles) {
                    if (!profile.isEncrypted())
                        profile = ProfileEncryptionAdapter.encryptProfile(profile);
                    objectOutputStream.writeObject(profile);
                }
                objectOutputStream.close();
                fileOutputStream.close();
            } catch (IOException | ProfileCryptException e) {
                SSHProxySwitcher.reportError(Thread.currentThread(), e);
            }

            for (LoadedProfilesListener listener : listeners)
                listener.loadedProfilesUpdated(loadedProfiles);
        }
    }

    public void loadProfiles() {
        loadedProfiles = new ArrayList<>();

        if (!profilesFile.exists() || profilesFile.length() == 0)
            return;

        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;

        try {
            fileInputStream = new FileInputStream(profilesFile);
            objectInputStream = new ObjectInputStream(fileInputStream);
            int profilesCount = objectInputStream.readInt();

            for (int i = 0; i < profilesCount; i++) {
                Profile loadedProfile = (Profile) objectInputStream.readObject();

                if (loadedProfile.isEncrypted())
                    loadedProfile = ProfileEncryptionAdapter.decryptProfile(loadedProfile);

                loadedProfiles.add(loadedProfile);
            }
        } catch (InvalidClassException e) {
            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.SEVERE, "Profiles saved are not compatible with the Profile object. Deleting saved profiles.");
            try {
                fileInputStream.close();
                if (objectInputStream != null)
                    objectInputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                Platform.exit();
            }
            boolean deleted = profilesFile.delete();
            if (!deleted) {
                SSHProxySwitcher.getInstance().getLoggingManager().log(Level.SEVERE, "Unable to delete profiles save file. Shutting down.");
                Platform.exit();
            } else
                loadProfiles();
        } catch (EOFException e) {
            return;
        } catch (IOException | ClassNotFoundException | ProfileCryptException e) {
            SSHProxySwitcher.reportError(Thread.currentThread(), e);
        }

        for (LoadedProfilesListener listener : listeners)
            listener.loadedProfilesUpdated(loadedProfiles);
    }

    public ArrayList<Profile> getLoadedProfiles() {
        return loadedProfiles;
    }

    public void addProfile(Profile profileToAdd) {
        if (loadedProfiles == null)
            loadProfiles();

        loadedProfiles.add(profileToAdd);

        saveProfiles();
    }

    public void deleteProfile(Profile profileToDelete) {
        if (loadedProfiles == null)
            loadProfiles();

        if (connectedProfile != null && connectedProfile.equals(profileToDelete))
            disconnectProfiles();

        Iterator<Profile> profileIterator = loadedProfiles.iterator();
        while (profileIterator.hasNext()) {
            Profile profile = profileIterator.next();
            if (profile.getProfileName().equals(profileToDelete.getProfileName()))
                profileIterator.remove();
        }

        saveProfiles();
    }

    public void connectProfile(Profile profile) {
        disconnectProfiles();
        if (profile != null) {
            SSHProxySwitcher.getInstance().getTrayIconManager().setStatus(TrayIconManager.STATUS_CONNECTING);
            try {
                if (profile.shouldConnectToSsh())
                    SSHProxySwitcher.getInstance().getSSHManager().startConnection(profile);
                if (profile.shouldAutoEnableProxy())
                    SSHProxySwitcher.getInstance().getProxyManager().setProxySettings(profile);
                connectedProfile = profile;
                SSHProxySwitcher.getInstance().getTrayIconManager().setStatus(TrayIconManager.STATUS_CONNECTED);
                if (profile.shouldConnectToSsh())
                    if (SSHProxySwitcher.getInstance().getPropertiesManager().getPropertyAsBool(PropertiesEnum.NOTIFY_CONNECT))
                        SSHProxySwitcher.getInstance().getTrayIconManager().displayMessage("Connected!", "Connection to " + profile.getProfileName() + " has been established.");
                    else if (profile.shouldAutoEnableProxy())
                        if (SSHProxySwitcher.getInstance().getPropertiesManager().getPropertyAsBool(PropertiesEnum.NOTIFY_CONNECT))
                            SSHProxySwitcher.getInstance().getTrayIconManager().displayMessage("Proxy Enabled!", "The proxy for " + profile.getProfileName() + " has been enabled.");
            } catch (SSHConnectionException e) {
                SSHProxySwitcher.getInstance().getTrayIconManager().setStatus(TrayIconManager.STATUS_ERROR);
                if (SSHProxySwitcher.getInstance().getPropertiesManager().getPropertyAsBool(PropertiesEnum.NOTIFY_CONNECT_FAIL))
                    SSHProxySwitcher.getInstance().getTrayIconManager().displayError("Connection Failed", "Could not connect to " + profile.getProfileName() + ": " + e.getMessage());
            } catch (ProxySettingsException e) {
                SSHProxySwitcher.getInstance().getTrayIconManager().setStatus(TrayIconManager.STATUS_ERROR);
                SSHProxySwitcher.reportError(Thread.currentThread(), e);
            }
        }
    }

    public void disconnectProfiles() {
        if (connectedProfile != null) {
            SSHProxySwitcher.getInstance().getSSHManager().stopConnection();
            SSHProxySwitcher.getInstance().getProxyManager().disableProxySettings();
            if (SSHProxySwitcher.getInstance().getPropertiesManager().getPropertyAsBool(PropertiesEnum.NOTIFY_DISCONNECT))
                SSHProxySwitcher.getInstance().getTrayIconManager().displayMessage("Disconnected", "Disconnected from " + connectedProfile.getProfileName());
            connectedProfile = null;
        }
        SSHProxySwitcher.getInstance().getTrayIconManager().setStatus(TrayIconManager.STATUS_DEFAULT);
    }

    public Profile getConnectedProfile() {
        return this.connectedProfile;
    }

    public void addLoadedProfilesListener(LoadedProfilesListener listener) {
        listeners.add(listener);
    }

    public Profile getProfileByName(String profileName) {
        for (Profile profile : loadedProfiles) {
            if (profile.getProfileName().equals(profileName))
                return profile;
        }

        return null;
    }

    public interface LoadedProfilesListener {
        void loadedProfilesUpdated(ArrayList<Profile> loadedProfiles);
    }
}