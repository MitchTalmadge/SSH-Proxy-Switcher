package com.mitchtalmadge.sshproxyswitcher.gui.controllers;

import com.mitchtalmadge.sshproxyswitcher.SSHProxySwitcher;
import com.mitchtalmadge.sshproxyswitcher.managers.profiles.Profile;
import com.mitchtalmadge.sshproxyswitcher.managers.profiles.ProfileManager;
import com.mitchtalmadge.sshproxyswitcher.managers.properties.PropertiesEnum;
import com.mitchtalmadge.sshproxyswitcher.managers.properties.PropertiesManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SettingsController implements Initializable, ProfileManager.LoadedProfilesListener {

    @FXML
    private CheckBox notifyConnectSuccess;

    @FXML
    private CheckBox notifyConnectFail;

    @FXML
    private CheckBox notifyConnectLost;

    @FXML
    private CheckBox notifyDisconnect;

    @FXML
    private CheckBox notifyReconnect;

    @FXML
    private CheckBox notifyReconnectFail;

    @FXML
    private ComboBox<String> startupProfileCombo;

    @FXML
    private CheckBox startWithWindows;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PropertiesManager propertiesManager = SSHProxySwitcher.getInstance().getPropertiesManager();

        notifyConnectSuccess.setSelected(propertiesManager.getPropertyAsBool(PropertiesEnum.NOTIFY_CONNECT));
        notifyConnectFail.setSelected(propertiesManager.getPropertyAsBool(PropertiesEnum.NOTIFY_CONNECT_FAIL));
        notifyConnectLost.setSelected(propertiesManager.getPropertyAsBool(PropertiesEnum.NOTIFY_CONNECT_LOST));
        notifyDisconnect.setSelected(propertiesManager.getPropertyAsBool(PropertiesEnum.NOTIFY_DISCONNECT));
        notifyReconnect.setSelected(propertiesManager.getPropertyAsBool(PropertiesEnum.NOTIFY_RECONNECT));
        notifyReconnectFail.setSelected(propertiesManager.getPropertyAsBool(PropertiesEnum.NOTIFY_RECONNECT_FAIL));

        updateProfilesCombo();

        startWithWindows.setSelected(propertiesManager.getPropertyAsBool(PropertiesEnum.START_WITH_WINDOWS));

        SSHProxySwitcher.getInstance().getProfileManager().addLoadedProfilesListener(this);
    }

    private void updateProfilesCombo() {
        PropertiesManager propertiesManager = SSHProxySwitcher.getInstance().getPropertiesManager();

        startupProfileCombo.getItems().clear();
        startupProfileCombo.getItems().add("");
        for (Profile profile : SSHProxySwitcher.getInstance().getProfileManager().getLoadedProfiles()) {
            startupProfileCombo.getItems().add(profile.getProfileName());
        }
        if (startupProfileCombo.getItems().contains(propertiesManager.getPropertyAsString(PropertiesEnum.AUTO_CONNECT_PROFILE)))
            startupProfileCombo.getSelectionModel().select(propertiesManager.getPropertyAsString(PropertiesEnum.AUTO_CONNECT_PROFILE));
        else
            startupProfileCombo.getSelectionModel().select(0);
    }

    @FXML
    void onCheckboxChanged(ActionEvent event) {
        PropertiesManager propertiesManager = SSHProxySwitcher.getInstance().getPropertiesManager();

        propertiesManager.setProperty(PropertiesEnum.NOTIFY_CONNECT, notifyConnectSuccess.isSelected() + "");
        propertiesManager.setProperty(PropertiesEnum.NOTIFY_CONNECT_FAIL, notifyConnectFail.isSelected() + "");
        propertiesManager.setProperty(PropertiesEnum.NOTIFY_CONNECT_LOST, notifyConnectLost.isSelected() + "");
        propertiesManager.setProperty(PropertiesEnum.NOTIFY_DISCONNECT, notifyDisconnect.isSelected() + "");
        propertiesManager.setProperty(PropertiesEnum.NOTIFY_RECONNECT, notifyReconnect.isSelected() + "");
        propertiesManager.setProperty(PropertiesEnum.NOTIFY_RECONNECT_FAIL, notifyReconnectFail.isSelected() + "");

        propertiesManager.setProperty(PropertiesEnum.START_WITH_WINDOWS, startWithWindows.isSelected() + "");
    }

    @FXML
    void onStartupProfileChanged(ActionEvent event) {
        PropertiesManager propertiesManager = SSHProxySwitcher.getInstance().getPropertiesManager();

        if (startupProfileCombo.getSelectionModel().getSelectedItem() == null)
            return;

        propertiesManager.setProperty(PropertiesEnum.AUTO_CONNECT_PROFILE, startupProfileCombo.getSelectionModel().getSelectedItem());
    }

    @Override
    public void loadedProfilesUpdated(ArrayList<Profile> loadedProfiles) {
        updateProfilesCombo();
    }
}
