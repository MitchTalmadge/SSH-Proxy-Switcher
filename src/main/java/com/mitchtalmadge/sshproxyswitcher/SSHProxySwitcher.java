package com.mitchtalmadge.sshproxyswitcher;

import javafx.application.Application;
import javafx.stage.Stage;
import com.mitchtalmadge.sshproxyswitcher.Managers2.LoggingManager2.LoggingManager;
import com.mitchtalmadge.sshproxyswitcher.Managers2.ProfileManager2.ProfileManager;
import com.mitchtalmadge.sshproxyswitcher.Managers2.PropertiesManager2.PropertiesException;
import com.mitchtalmadge.sshproxyswitcher.Managers2.PropertiesManager2.PropertiesManager;
import com.mitchtalmadge.sshproxyswitcher.Managers2.ProxyManager2.ProxyManager;
import com.mitchtalmadge.sshproxyswitcher.Managers2.SSHManager2.SSHManager;
import com.mitchtalmadge.sshproxyswitcher.Utilities2.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.prefs.Preferences;

public class SSHProxySwitcher extends Application {

    private static final File LOG_DIR = new File(FileUtilities.getRootDirectory(), "logs");
    private static final File XML_FILE = new File(FileUtilities.getRootDirectory(), "profiles.xml");
    private static final File PROPERTIES_FILE = new File(FileUtilities.getRootDirectory(), "SSHProxySwitcher.config");
    private GUIHelper guiHelper;
    private LoggingManager loggingManager;
    private PropertiesManager propertiesManager;
    private ProfileManager profileManager;
    private SSHManager sshManager;
    private ProxyManager proxyManager;

    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.guiHelper = new GUIHelper();

        if (!isWindows()) {
            guiHelper.showErrorDialog("Error", "Windows Only", "SSH Proxy Switcher only works on Windows. This program will now close.");
            System.exit(1);
        }
        if (!isRunningAsAdmin()) {
            guiHelper.showErrorDialog("Error", "Administrator Privileges Required", "SSH Proxy Switcher requires Administrative Privileges. This program will now close.");
            System.exit(2);
        }

        this.loggingManager = new LoggingManager();
        loggingManager.startLogging(LOG_DIR);

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
        synchronized (System.err) {
            System.setErr(new PrintStream(new OutputStream() {
                @Override
                public void write(int i) throws IOException {
                }
            }));
            try {
                prefs.put("SSHProxySwitcher_AdminChecker", "Success"); // SecurityException on Windows
                prefs.remove("SSHProxySwitcher_AdminChecker");
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
