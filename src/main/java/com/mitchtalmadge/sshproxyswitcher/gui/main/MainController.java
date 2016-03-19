package com.mitchtalmadge.sshproxyswitcher.gui.main;

import com.mitchtalmadge.sshproxyswitcher.Versioning;
import com.mitchtalmadge.sshproxyswitcher.managers.profiles.Profile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    /**
     * The header label.
     */
    @FXML
    private Label headerLabel;

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
    private TextField profileNameField;

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
    private TextField sshHostNameField;

    /**
     * A field containing the SSH Port - Optional - Default: 22
     */
    @FXML
    private TextField sshPortField;

    /**
     * A field containing the SSH Username
     */
    @FXML
    private TextField sshUsernameField;

    /**
     * A field containing the SSH Password - Optional
     */
    @FXML
    private TextField sshPasswordField;

    /**
     * A field containing the SSH RSA Key Path. Should be set by the browse button. The user does not have access to edit this field.
     */
    @FXML
    private TextField sshRsaKeyPathField;

    /**
     * A field containing the SSH RSA Key Password - Optional
     */
    @FXML
    private TextField sshRsaKeyPasswordField;

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
    private TextField proxyHostNameField;

    /**
     * The Port of the proxy.
     */
    @FXML
    private TextField proxyPortField;

    /* END PROXY CONFIG */

    /**
     * The panel containing the delete/save buttons.
     */
    @FXML
    private GridPane configurationButtons;

    /* END CONFIGURATION */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //this.headerLabel.setGraphic(new ImageView(Versioning.getLogoAsJavaFXImage()));
    }

    /**
     * Called when the Create New Profile button is clicked.
     */
    @FXML
    void onCreateButtonFired(ActionEvent event) {

    }

    /**
     * Called when the connectToSSHCheck checkbox is checked/unchecked.
     */
    @FXML
    void onConnectToSSHCheckFired(ActionEvent event) {

    }

    /**
     * Called when the "Browse..." button for the RSA Private Key File is clicked.
     */
    @FXML
    void onSshRsaBrowseButtonFired(ActionEvent event) {

    }

    /**
     * Called when the proxyTunnelCheck checkbox is checked/unchecked.
     */
    @FXML
    void onProxyTunnelCheckFired(ActionEvent event) {

    }

    /**
     * Called when the proxyAutosetCheck checkbox is checked/unchecked.
     */
    @FXML
    void onProxyAutosetCheckFired(ActionEvent event) {

    }

    /**
     * Called when the delete profile button is clicked.
     */
    @FXML
    void onDeleteButtonFired(ActionEvent event) {

    }

    /**
     * Called when the save profile button is clicked.
     */
    @FXML
    void onSaveButtonFired(ActionEvent event) {

    }

}
