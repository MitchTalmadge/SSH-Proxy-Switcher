package net.liveforcode.SSHProxySwitcher;

import net.liveforcode.SSHProxySwitcher.Managers.ProfileManager;
import net.liveforcode.SSHProxySwitcher.Managers.SSHManager.SSHManager;
import net.liveforcode.SSHProxySwitcher.Utilities.FileUtilities;

import java.io.File;

public class SSHProxySwitcher {

    private final File xmlFile;
    private ProfileManager profileManager;
    private SSHManager sshManager;

    public SSHProxySwitcher() {
        this.xmlFile = new File(FileUtilities.getRootDirectory(), "profiles.xml");
    }

    public static void main(String... args) {
        SSHProxySwitcher sshProxySwitcher = new SSHProxySwitcher();
        sshProxySwitcher.init();
    }

    public void init() {
        this.profileManager = new ProfileManager();
        profileManager.loadProfilesFromXmlFile(this.xmlFile);
        this.sshManager = new SSHManager();
    }

    public ProfileManager getProfileManager() {
        return this.profileManager;
    }

    public SSHManager getSSHManager() {
        return this.sshManager;
    }
}
