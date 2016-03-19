package com.mitchtalmadge.sshproxyswitcher.GUI;

import com.mitchtalmadge.sshproxyswitcher.GUIHelper;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class GUIHelperTest {

    private GUIHelper guiHelper;

    @Before
    public void setUp() throws Exception {
        this.guiHelper = new GUIHelper();
        assertNotNull("GUIHelper is null", guiHelper);
        new JFXPanel();
    }

    @Test
    public void testShowInformationDialog() throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                guiHelper.showInformationDialog("Test", "Test", "Test");
            }
        });
    }

    @Test
    public void testShowWarningDialog() throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                guiHelper.showWarningDialog("Test", "Test", "Test");
            }
        });
    }

    @Test
    public void testShowErrorDialog() throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                guiHelper.showErrorDialog("Test", "Test", "Test");
            }
        });
    }
}