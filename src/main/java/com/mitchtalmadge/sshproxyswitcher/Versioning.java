package com.mitchtalmadge.sshproxyswitcher;

import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Versioning {

    public static final String VERSION_STRING = "1.0";
    public static final String PROGRAM_NAME = "SSH Proxy Switcher";
    public static final String PROGRAM_NAME_WITH_VERSION = PROGRAM_NAME + " V" + VERSION_STRING;
    public static final String PROGRAM_NAME_NO_SPACES = "SSH-Proxy-Switcher";
    public static final int APTIAPI_PROJECT_ID = 2;
    private static String logoImageLocation = "/images/Logo-32px.png";
    private static String logoTrayIconImageLocation = "/images/Logo-16px.png";
    private static String logoTrayIconGreenImageLocation = "/images/Logo-16px-Green.png";
    private static String logoTrayIconRedImageLocation = "/images/Logo-16px-Red.png";
    private static BufferedImage logoImage = null;
    private static BufferedImage logoTrayIconImage = null;
    private static BufferedImage logoTrayIconImageGreen = null;
    private static BufferedImage logoTrayIconImageRed = null;

    static {
        try {
            logoImage = ImageIO.read(Versioning.class.getResource(logoImageLocation));
            logoTrayIconImage = ImageIO.read(Versioning.class.getResource(logoTrayIconImageLocation));
            logoTrayIconImageGreen = ImageIO.read(Versioning.class.getResource(logoTrayIconGreenImageLocation));
            logoTrayIconImageRed = ImageIO.read(Versioning.class.getResource(logoTrayIconRedImageLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ImageIcon getLogoAsImageIcon() {
        return new ImageIcon(logoImage);
    }

    public static BufferedImage getLogoAsBufferedImage() {
        return logoImage;
    }

    public static BufferedImage getLogoTrayIcon() {
        return logoTrayIconImage;
    }

    public static Image getLogoAsJavaFXImage() {
        return new Image(Versioning.class.getResourceAsStream(logoImageLocation));
    }
}
