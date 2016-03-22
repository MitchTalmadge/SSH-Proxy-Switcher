package com.mitchtalmadge.sshproxyswitcher.gui.controllers;

import com.mitchtalmadge.sshproxyswitcher.SSHProxySwitcher;
import com.mitchtalmadge.sshproxyswitcher.managers.logging.LoggingManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class LogController implements Initializable, LoggingManager.LogListener {

    @FXML
    private TextArea logTextArea;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SSHProxySwitcher.getInstance().getLoggingManager().addLogListener(this);
    }

    @FXML
    void onOpenLogsDirectoryButtonFired(ActionEvent event) {
        try {
            Desktop.getDesktop().browse(SSHProxySwitcher.LOG_DIR.toURI());
        } catch (IOException e) {
            SSHProxySwitcher.reportError(Thread.currentThread(), e);
        }
    }

    @Override
    public void log(Level logLevel, String message) {
        if (logLevel.intValue() > Level.FINE.intValue())
            logTextArea.appendText(message);
    }
}
