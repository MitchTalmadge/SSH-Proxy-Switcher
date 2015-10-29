package net.liveforcode.SSHProxySwitcher;

import net.liveforcode.SSHProxySwitcher.GUI.MainInterface;
import net.liveforcode.SSHProxySwitcher.Utilities.FileUtilities;

import java.io.File;
import java.util.ArrayList;

public class SSHProxySwitcher implements ProfileManager.ProfileListener {

    private ProfileManager profileManager;
    MainInterface mainInterface;

    public static final File PROFILES_XML_FILE = new File(FileUtilities.getRootDirectory(), "profiles.xml");

    public void startUp() {
        profileManager = new ProfileManager(PROFILES_XML_FILE);
        profileManager.loadProfilesFromXML();
        profileManager.addProfileListener(this);
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public void showMainInterface() {
        if (this.mainInterface == null)
            mainInterface = new MainInterface(this);
        mainInterface.setVisible(true);
    }

    @Override
    public void onProfilesReloaded(ArrayList<Profile> loadedProfiles) {
        if (mainInterface != null) {
            mainInterface.refreshProfileList();
        }
    }

    @Override
    public void onProfileUpdated(Profile profile) {

    }

    @Override
    public void onProfileAdded(Profile profile) {

    }

    @Override
    public void onProfileRemoved(Profile profile) {

    }
}
