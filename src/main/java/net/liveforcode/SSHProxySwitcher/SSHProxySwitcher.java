package net.liveforcode.SSHProxySwitcher;

import net.liveforcode.SSHProxySwitcher.Managers.LoggingManager.LoggingException;
import net.liveforcode.SSHProxySwitcher.Managers.LoggingManager.LoggingManager;
import net.liveforcode.SSHProxySwitcher.Managers.ProfileManager.ProfileManager;
import net.liveforcode.SSHProxySwitcher.Managers.PropertiesManager.PropertiesException;
import net.liveforcode.SSHProxySwitcher.Managers.PropertiesManager.PropertiesManager;
import net.liveforcode.SSHProxySwitcher.Managers.ProxyManager.ProxyManager;
import net.liveforcode.SSHProxySwitcher.Managers.SSHManager.SSHManager;
import net.liveforcode.SSHProxySwitcher.Utilities.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.prefs.Preferences;

public class SSHProxySwitcher {

    private static final File LOG_DIR = new File(FileUtilities.getRootDirectory(), "logs");
    private static final File XML_FILE = new File(FileUtilities.getRootDirectory(), "profiles.xml");
    private static final File PROPERTIES_FILE = new File(FileUtilities.getRootDirectory(), "SSHProxySwitcher.config");
    private LoggingManager loggingManager;
    private PropertiesManager propertiesManager;
    private ProfileManager profileManager;
    private SSHManager sshManager;
    private ProxyManager proxyManager;

    public SSHProxySwitcher() {
        if (!isWindows()) {
            System.err.println("SSH Proxy Switcher only works on Windows.");
            System.exit(1);
        }
        if (!isRunningAsAdmin()) {
            System.err.println("SSH Proxy Switcher requires Administrator Privileges");
            System.exit(2);
        }
    }

    public static void main(String... args) {
        SSHProxySwitcher sshProxySwitcher = new SSHProxySwitcher();
        sshProxySwitcher.init();
    }

    public void init() {
        this.loggingManager = new LoggingManager();
        try {
            loggingManager.startLogging(LOG_DIR);
        } catch (LoggingException e) {
            e.printStackTrace();
        }
        loggingManager.log(Level.INFO, "Starting SSHProxySwitcher");

        loggingManager.log(Level.INFO, "Reading Config File");
        this.propertiesManager = new PropertiesManager();
        try {
            propertiesManager.loadPropertiesFromFile(PROPERTIES_FILE);
        } catch (PropertiesException e) {
            e.printStackTrace();
        }

        loggingManager.log(Level.INFO, "Reading Profiles");
        this.profileManager = new ProfileManager();
        profileManager.loadProfilesFromXmlFile(XML_FILE);

        loggingManager.log(Level.INFO, "Starting SSH Service");
        this.sshManager = new SSHManager();

        loggingManager.log(Level.INFO, "Starting Proxy Service");
        this.proxyManager = new ProxyManager();

        loggingManager.log(Level.INFO, "SSHProxySwitcher is running");
    }

    public ProfileManager getProfileManager() {
        return this.profileManager;
    }

    public SSHManager getSSHManager() {
        return this.sshManager;
    }

    private boolean isWindows() {
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
