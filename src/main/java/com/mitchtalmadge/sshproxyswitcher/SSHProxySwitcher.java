package com.mitchtalmadge.sshproxyswitcher;

import com.mitchtalmadge.sshproxyswitcher.gui.GUIHelper;
import com.mitchtalmadge.sshproxyswitcher.gui.TrayIconManager;
import com.mitchtalmadge.sshproxyswitcher.managers.logging.LoggingManager;
import com.mitchtalmadge.sshproxyswitcher.managers.profiles.ProfileManager;
import com.mitchtalmadge.sshproxyswitcher.managers.properties.PropertiesException;
import com.mitchtalmadge.sshproxyswitcher.managers.properties.PropertiesManager;
import com.mitchtalmadge.sshproxyswitcher.managers.proxies.ProxyManager;
import com.mitchtalmadge.sshproxyswitcher.managers.ssh.SSHManager;
import com.mitchtalmadge.sshproxyswitcher.utilities.FileUtilities;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
    private static SSHProxySwitcher instance;
    private LoggingManager loggingManager;
    private PropertiesManager propertiesManager;
    private ProfileManager profileManager;
    private SSHManager sshManager;
    private ProxyManager proxyManager;
    private TrayIconManager trayIconManager;

    private Stage stage;

    public static SSHProxySwitcher getInstance() {
        return instance;
    }

    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;

        if (!isWindows()) {
            GUIHelper.showErrorDialog("Error", "Windows Only", "SSH Proxy Switcher only works on Windows. This program will now close.");
            System.exit(1);
        }
        if (!isRunningAsAdmin()) {
            GUIHelper.showErrorDialog("Error", "Administrator Privileges Required", "SSH Proxy Switcher requires Administrative Privileges. This program will now close.");
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
        profileManager.loadProfiles();

        loggingManager.log(Level.INFO, "Starting SSH Service");
        this.sshManager = new SSHManager();

        loggingManager.log(Level.INFO, "Starting Proxy Service");
        this.proxyManager = new ProxyManager();

        loggingManager.log(Level.INFO, "Starting Tray Icon Service");
        this.trayIconManager = new TrayIconManager();

        loggingManager.log(Level.INFO, "Loading User Interface");
        Platform.setImplicitExit(false); //Prevent application from closing when window is hidden.

        this.stage = primaryStage;
        stage.setTitle(Versioning.PROGRAM_NAME);
        stage.setResizable(false);
        stage.getIcons().add(Versioning.getLogoAsJavaFXImage());
        stage.setOnCloseRequest(event -> {
            loggingManager.log(Level.INFO, "Hiding Control Panel");
            stage.hide();
            event.consume();
        });

        Parent root = FXMLLoader.load(getClass().getResource("/gui/main.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);

        loggingManager.log(Level.INFO, "SSH Proxy Switcher is running");
    }

    @Override
    public void stop() throws Exception {
        loggingManager.log(Level.INFO, "SSH Proxy Switcher is shutting down");

        stage.hide();

        loggingManager.log(Level.INFO, "Shutting down all SSH Connections");
        sshManager.stopConnection();

        loggingManager.log(Level.INFO, "Saving properties");
        propertiesManager.saveProperties();

        loggingManager.log(Level.INFO, "Closing Logger");
        loggingManager.stopLogging();

        System.exit(0);
    }

    public ProfileManager getProfileManager() {
        return this.profileManager;
    }

    public SSHManager getSSHManager() {
        return this.sshManager;
    }

    public LoggingManager getLoggingManager() {
        return loggingManager;
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

    public void showMainWindow() {
        Platform.runLater(() -> {
            if (stage != null) {
                loggingManager.log(Level.INFO, "Showing Control Panel");
                stage.show();
            }
        });
    }

    public ProxyManager getProxyManager() {
        return proxyManager;
    }
}
