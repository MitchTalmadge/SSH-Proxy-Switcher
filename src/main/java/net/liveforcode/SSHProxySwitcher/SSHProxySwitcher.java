package net.liveforcode.SSHProxySwitcher;

import net.liveforcode.SSHProxySwitcher.Managers.ProfileManager.ProfileManager;
import net.liveforcode.SSHProxySwitcher.Managers.SSHManager.SSHManager;
import net.liveforcode.SSHProxySwitcher.Utilities.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.prefs.Preferences;

public class SSHProxySwitcher {

    private final File xmlFile = new File(FileUtilities.getRootDirectory(), "profiles.xml");
    private ProfileManager profileManager;
    private SSHManager sshManager;

    public SSHProxySwitcher() {
        if(!isWindows())
        {
            System.err.println("SSH Proxy Switcher only works on Windows.");
            System.exit(1);
        }
        if(!isRunningAsAdmin())
        {
            System.err.println("SSH Proxy Switcher requires Administrator Privileges");
            System.exit(2);
        }
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

    private boolean isWindows()
    {
        return System.getProperty("os.name").startsWith("Windows");
    }

    private boolean isRunningAsAdmin() {
        Preferences prefs = Preferences.systemRoot();
        PrintStream systemErr = System.err;
        synchronized (systemErr) {
            System.setErr(new PrintStream(new OutputStream() {
                @Override
                public void write(int i) throws IOException {
                }
            }));
            try {
                prefs.put("foo", "bar"); // SecurityException on Windows
                prefs.remove("foo");
                prefs.flush(); // BackingStoreException on Linux
                return true;
            } catch (Exception e) {
                return false;
            } finally {
                System.setErr(systemErr);
            }
        }
    }
}
