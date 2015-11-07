package net.liveforcode.SSHProxySwitcher;

import javafx.scene.image.*;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Versioning {

    public static final String VERSION_STRING = "1.0";
    public static final String PROGRAM_NAME = "SSH Proxy Switcher";
    public static final String PROGRAM_NAME_WITH_VERSION = PROGRAM_NAME + " V" + VERSION_STRING;
    public static final String PROGRAM_NAME_NO_SPACES = "SSH-Proxy-Switcher";
    public static final int APTIAPI_PROJECT_ID = 2;
    private static String logoImageLocation = "/images/logo-64px.png";
    private static BufferedImage logoImage = null;

    static {
        try {
            logoImage = ImageIO.read(Versioning.class.getResource(logoImageLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ImageIcon getLogoAsImageIcon() {
        return new ImageIcon(logoImage);
    }

    public static BufferedImage getLogoAsBufferedImage()
    {
        return logoImage;
    }

    public static Image getLogoAsJavaFXImage()
    {
        return new Image(Versioning.class.getResourceAsStream(logoImageLocation));
    }
}
