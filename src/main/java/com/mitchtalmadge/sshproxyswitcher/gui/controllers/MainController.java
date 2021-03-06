package com.mitchtalmadge.sshproxyswitcher.gui.controllers;

import com.mitchtalmadge.sshproxyswitcher.SSHProxySwitcher;
import com.mitchtalmadge.sshproxyswitcher.gui.GUIHelper;
import com.mitchtalmadge.sshproxyswitcher.gui.LimitingTextField;
import com.mitchtalmadge.sshproxyswitcher.managers.profiles.Profile;
import com.mitchtalmadge.sshproxyswitcher.managers.profiles.ProfileManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable, ProfileManager.LoadedProfilesListener, ChangeListener<Profile> {

    /**
     * The header label.
     */
    @FXML
    private Label headerLabel;

    @FXML
    private Tab logsTab;

    @FXML
    private Tab settingsTab;

    /**
     * The entire Configuration Panel
     */
    @FXML
    private BorderPane configurationPane;

    /**
     * A tree containing a list of saved profiles.
     */
    @FXML
    private ListView<Profile> profileListView;

    /* BEGIN CONFIGURATION SECTION */

    /**
     * A field containing the name of the profile.
     */
    @FXML
    private LimitingTextField profileNameField;

    /* BEGIN SSH CONFIG */

    /**
     * A checkbox that controls whether ssh is enabled or not.
     */
    @FXML
    private CheckBox connectToSSHCheck;

    /**
     * The panel containing all SSH configuration options. Should be disabled if SSH is disabled.
     */
    @FXML
    private VBox sshServerConfiguration;

    /**
     * A field containing the SSH Host Name
     */
    @FXML
    private LimitingTextField sshHostNameField;

    /**
     * A field containing the SSH Port - Optional - Default: 22
     */
    @FXML
    private LimitingTextField sshPortField;

    /**
     * A field containing the SSH Username
     */
    @FXML
    private LimitingTextField sshUsernameField;

    /**
     * A field containing the SSH Password - Optional
     */
    @FXML
    private PasswordField sshPasswordField;

    /**
     * A field containing the SSH RSA Key Path. Should be set by the browse button. The user does not have access to edit this field.
     */
    @FXML
    private TextField sshRsaKeyPathField;

    /**
     * A field containing the SSH RSA Key Password - Optional
     */
    @FXML
    private PasswordField sshRsaKeyPasswordField;

    /* END SSH CONFIG */

    /* BEGIN PROXY CONFIG */

    /**
     * A checkbox that determines whether a proxy tunnel should be enabled or not.
     */
    @FXML
    private CheckBox proxyTunnelCheck;

    /**
     * A checkbox that determines whether the proxy settings should be applied automatically or not.
     */
    @FXML
    private CheckBox proxyAutosetCheck;

    /**
     * The panel containing all the proxy settings.
     */
    @FXML
    private VBox proxyConfiguration;

    /**
     * The Host Name of the proxy.
     */
    @FXML
    private LimitingTextField proxyHostNameField;

    /**
     * The Port of the proxy.
     */
    @FXML
    private LimitingTextField proxyPortField;

    /* END PROXY CONFIG */

    /**
     * The panel containing the delete/save buttons.
     */
    @FXML
    private GridPane configurationButtons;

    /* END CONFIGURATION */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SSHProxySwitcher.getInstance().getProfileManager().addLoadedProfilesListener(this);

        //Add Tabs
        try {
            this.logsTab.setContent(FXMLLoader.load(getClass().getResource("/gui/logs.fxml")));
            this.settingsTab.setContent(FXMLLoader.load(getClass().getResource("/gui/settings.fxml")));
        } catch (IOException e) {
            SSHProxySwitcher.reportError(Thread.currentThread(), e);
        }

        //Set up limiting text fields
        this.sshHostNameField.setRegexLimiter("[a-zA-Z0-9\\.]*");
        this.sshPortField.setRegexLimiter("[0-9]*");
        this.sshPortField.setMaxLength(5);
        this.sshUsernameField.setRegexLimiter("[^\\s]*");

        this.proxyHostNameField.setRegexLimiter("[a-zA-Z0-9\\.]*");
        this.proxyPortField.setRegexLimiter("[0-9]*");
        this.proxyPortField.setMaxLength(5);

        profileListView.getSelectionModel().selectedItemProperty().addListener(this);
        refreshProfilesList();
    }

    private void refreshProfilesList() {
        ArrayList<Profile> profilesList = SSHProxySwitcher.getInstance().getProfileManager().getLoadedProfiles();
        if (profilesList != null) {
            profileListView.getItems().clear();
            profileListView.getItems().addAll(profilesList);
            if (profilesList.size() == 0)
                configurationPane.setVisible(false);
            else
                profileListView.getSelectionModel().select(0);
        }
    }

    private void setConfigurationFields(Profile profile) {
        this.profileNameField.setText(profile.getProfileName());

        this.connectToSSHCheck.setSelected(profile.shouldConnectToSsh());
        this.sshHostNameField.setText(profile.getSshHostName());
        this.sshPortField.setText(profile.getSshHostPort() > 0 ? profile.getSshHostPort() + "" : "");
        this.sshUsernameField.setText(profile.getSshUsername());
        this.sshPasswordField.setText(profile.getSshPassword());
        this.sshRsaKeyPathField.setText(profile.getSshRsaPrivateKeyFilePath());
        this.sshRsaKeyPasswordField.setText(profile.getSshRsaPrivateKeyPassword());

        this.proxyAutosetCheck.setSelected(profile.shouldAutoEnableProxy());
        this.proxyTunnelCheck.setSelected(profile.shouldUseSshDynamicTunnel());

        this.proxyHostNameField.setText(profile.getProxyHostName());
        this.proxyPortField.setText(profile.getProxyPort() > 0 ? profile.getProxyPort() + "" : "");

        updateConfigurationDisabledComponents();

        configurationPane.setVisible(true);
    }

    private void updateConfigurationDisabledComponents() {
        this.sshServerConfiguration.setDisable(!connectToSSHCheck.isSelected());
        this.proxyTunnelCheck.setDisable(!connectToSSHCheck.isSelected());
        this.proxyConfiguration.setDisable(!(proxyAutosetCheck.isSelected() || (proxyTunnelCheck.isSelected() && !proxyTunnelCheck.isDisable())));
        this.proxyHostNameField.setDisable((proxyTunnelCheck.isSelected() && !proxyTunnelCheck.isDisable()));
    }

    /**
     * Called when the Create New Profile button is clicked.
     */
    @FXML
    void onCreateButtonFired(ActionEvent event) {
        String profileName = "New Profile";
        int nameTry = 1;

        boolean searching = true;
        while (searching) {
            searching = false;
            for (Profile profile : SSHProxySwitcher.getInstance().getProfileManager().getLoadedProfiles()) {
                if (profile.getProfileName().equals(profileName)) {
                    nameTry++;
                    profileName = "New Profile " + nameTry;
                    searching = true;
                }
            }
        }

        Profile profile = new Profile(profileName);
        profile.setSshHostPort(22);
        profile.setProxyHostName("127.0.0.1");
        profile.setProxyPort(2000);
        SSHProxySwitcher.getInstance().getProfileManager().addProfile(profile);

        profileListView.getSelectionModel().selectLast();
    }

    /**
     * Called when the connectToSSHCheck checkbox is checked/unchecked.
     */
    @FXML
    void onConnectToSSHCheckFired(ActionEvent event) {
        updateConfigurationDisabledComponents();
    }

    /**
     * Called when the "Browse..." button for the RSA Private Key File is clicked.
     */
    @FXML
    void onSshRsaBrowseButtonFired(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File chosenFile = fileChooser.showOpenDialog(sshRsaKeyPathField.getScene().getWindow());
        if (chosenFile == null)
            sshRsaKeyPathField.setText("");
        else {
            try {
                FileReader fileReader = new FileReader(chosenFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                if (bufferedReader.readLine().startsWith("PuTTY")) {
                    GUIHelper.showErrorDialog("Unsupported RSA Key", "Unsupported RSA Key", "PuTTY RSA keys are not supported. Please use PuTTYGen to convert the key to an OpenSSH format.");
                    sshRsaKeyPathField.setText("");
                } else
                    sshRsaKeyPathField.setText(chosenFile.getAbsolutePath());

                bufferedReader.close();
                fileReader.close();
            } catch (IOException e) {
                SSHProxySwitcher.reportError(Thread.currentThread(), e);
            }
        }
    }

    /**
     * Called when the proxyTunnelCheck checkbox is checked/unchecked.
     */
    @FXML
    void onProxyTunnelCheckFired(ActionEvent event) {
        updateConfigurationDisabledComponents();
    }

    /**
     * Called when the proxyAutosetCheck checkbox is checked/unchecked.
     */
    @FXML
    void onProxyAutosetCheckFired(ActionEvent event) {
        updateConfigurationDisabledComponents();
    }

    /**
     * Called when the delete profile button is clicked.
     */
    @FXML
    void onDeleteButtonFired(ActionEvent event) {
        Profile selectedProfile = profileListView.getSelectionModel().getSelectedItem();
        int selectedIndex = profileListView.getSelectionModel().getSelectedIndex();
        if (GUIHelper.getConfirmationFromDialog("Delete?", "Deletion Confirmation", "Are you sure you want to delete " + selectedProfile.getProfileName() + "?")) {
            SSHProxySwitcher.getInstance().getProfileManager().deleteProfile(selectedProfile);
        }

        if (profileListView.getItems().size() - 1 >= selectedIndex)
            profileListView.getSelectionModel().select(selectedIndex);
        else
            profileListView.getSelectionModel().select(selectedIndex > 0 ? selectedIndex - 1 : 0);
    }

    /**
     * Called when the save profile button is clicked.
     */
    @FXML
    void onSaveButtonFired(ActionEvent event) {
        for (Profile profile : SSHProxySwitcher.getInstance().getProfileManager().getLoadedProfiles()) {
            if (profile.getProfileName().equalsIgnoreCase(this.profileNameField.getText()) && !profile.equals(profileListView.getSelectionModel().getSelectedItem())) {
                GUIHelper.showErrorDialog("Cannot Save", "Profile Name is Taken", "The profile name you have entered is already taken. Please choose a unique name. Changes have not been saved.");
                return;
            }
        }

        Profile profile = this.profileListView.getSelectionModel().getSelectedItem();
        profile.setProfileName(this.profileNameField.getText());

        profile.setConnectToSsh(this.connectToSSHCheck.isSelected());

        profile.setSshHostName(this.sshHostNameField.getText());
        profile.setSshHostPort(this.sshPortField.getText().isEmpty() ? 0 : Integer.parseInt(this.sshPortField.getText()));
        profile.setSshUsername(this.sshUsernameField.getText());
        profile.setSshPassword(this.sshPasswordField.getText());
        profile.setSshRsaPrivateKeyFilePath(this.sshRsaKeyPathField.getText());
        profile.setSshRsaPrivateKeyPassword(this.sshRsaKeyPasswordField.getText());

        profile.setAutoEnableProxy(this.proxyAutosetCheck.isSelected());
        profile.setUseSshDynamicTunnel(this.proxyTunnelCheck.isSelected());

        profile.setProxyHostName(this.proxyHostNameField.getText());
        profile.setProxyPort(this.proxyPortField.getText().isEmpty() ? 0 : Integer.parseInt(this.proxyPortField.getText()));

        SSHProxySwitcher.getInstance().getProfileManager().saveProfiles();

        profileListView.getSelectionModel().select(profile);
    }

    @Override
    public void loadedProfilesUpdated(ArrayList<Profile> loadedProfiles) {
        refreshProfilesList();
    }

    @Override
    public void changed(ObservableValue<? extends Profile> observable, Profile oldValue, Profile newValue) {
        if (newValue != null)
            setConfigurationFields(newValue);
        else
            configurationPane.setVisible(false);
    }

}
