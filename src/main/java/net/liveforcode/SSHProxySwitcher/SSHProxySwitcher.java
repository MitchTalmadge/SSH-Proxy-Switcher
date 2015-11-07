package net.liveforcode.SSHProxySwitcher;

import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import net.liveforcode.SSHProxySwitcher.GUI.GUIHelper;
import net.liveforcode.SSHProxySwitcher.Managers.ProfileManager.ProfileManager;
import net.liveforcode.SSHProxySwitcher.Managers.PropertiesManager.PropertiesException;
import net.liveforcode.SSHProxySwitcher.Managers.PropertiesManager.PropertiesManager;
import net.liveforcode.SSHProxySwitcher.Managers.SSHManager.SSHManager;
import net.liveforcode.SSHProxySwitcher.Utilities.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.prefs.Preferences;

public class SSHProxySwitcher extends Application {

    private static final File XML_FILE = new File(FileUtilities.getRootDirectory(), "profiles.xml");
    private static final File PROPERTIES_FILE = new File(FileUtilities.getRootDirectory(), "SSHProxySwitcher.config");
    private GUIHelper guiHelper;
    private PropertiesManager propertiesManager;
    private ProfileManager profileManager;
    private SSHManager sshManager;

    public static void main(String... args) {
        Application.launch(SSHProxySwitcher.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.guiHelper = new GUIHelper();

        if (!isWindows()) {
            guiHelper.showErrorDialog("Error", "Windows Only", "SSH Proxy Switcher only works on Windows. This program will now close.");
            System.exit(1);
        }
        if (!isRunningAsAdmin()) {
            guiHelper.showErrorDialog("Error", "Administrator Privileges Required", "SSH Proxy Switcher requires Administrator Privileges. This program will now close.");
            System.exit(2);
        }

        this.propertiesManager = new PropertiesManager();
        try {
            propertiesManager.loadPropertiesFromFile(PROPERTIES_FILE);
        } catch (PropertiesException e) {
            e.printStackTrace();
        }
        this.profileManager = new ProfileManager();
        profileManager.loadProfilesFromXmlFile(XML_FILE);
        this.sshManager = new SSHManager();
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
