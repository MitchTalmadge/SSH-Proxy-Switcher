package net.liveforcode.SSHProxySwitcher;

import net.liveforcode.SSHProxySwitcher.GUI.MainInterface;
import net.liveforcode.SSHProxySwitcher.Utilities.FileUtilities;

import java.io.File;

public class SSHProxySwitcher {

    private final ProfileManager profileManager;
    MainInterface mainInterface;

    public SSHProxySwitcher() {
        profileManager = new ProfileManager(new File(FileUtilities.getRootDirectory(), "profiles.xml"));
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public void showMainInterface() {
        if (this.mainInterface == null)
            mainInterface = new MainInterface();
        mainInterface.setVisible(true);
    }
}
