package com.mitchtalmadge.sshproxyswitcher;

import javafx.scene.image.Image;
import org.junit.Test;

import javax.swing.*;

import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class VersioningTest {

    @Test
    public void testGetLogoAsImageIconReturnsImageIcon() throws Exception {
        ImageIcon imageIcon = Versioning.getLogoAsImageIcon();

        assertNotNull("ImageIcon is null", imageIcon);
    }

    @Test
    public void testGetLogoAsBufferedImageReturnsBufferedImage() throws Exception {
        BufferedImage bufferedImage = Versioning.getLogoAsBufferedImage();

        assertNotNull("BufferedImage is null", bufferedImage);
    }

    @Test
    public void testGetLogoAsJavaFXImageReturnsJavaFXImage() throws Exception {
        Image javaFXImage = Versioning.getLogoAsJavaFXImage();

        assertNotNull("JavaFX Image is null", javaFXImage);
    }
}