package net.liveforcode.SSHProxySwitcher;

import com.sun.xml.internal.bind.v2.TODO;
import net.liveforcode.SSHProxySwitcher.GUI.MainInterface;
import net.liveforcode.SSHProxySwitcher.Utilities.FileUtilities;

import java.io.File;
import java.util.ArrayList;

public class SSHProxySwitcher implements ProfileManager.ProfileListener {

    private final ProfileManager profileManager;
    MainInterface mainInterface;

    public SSHProxySwitcher() {
        profileManager = new ProfileManager(new File(FileUtilities.getRootDirectory(), "profiles.xml"));
        profileManager.addProfileListener(this);
        showMainInterface();
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
        if(mainInterface != null)
        {
            //TODO: Update Profile List
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
