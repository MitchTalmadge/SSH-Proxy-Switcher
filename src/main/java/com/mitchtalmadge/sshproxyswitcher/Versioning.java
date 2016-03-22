package com.mitchtalmadge.sshproxyswitcher;

import com.aptitekk.aptiapi.AptiAPIVersioningDetails;
import com.mitchtalmadge.sshproxyswitcher.gui.TrayIconImage;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Versioning implements AptiAPIVersioningDetails {

    public static final double VERSION = 1.0;
    public static final String VERSION_STRING = VERSION + "";
    public static final String PROGRAM_NAME = "SSH Proxy Switcher";
    public static final String PROGRAM_NAME_WITH_VERSION = PROGRAM_NAME + " V" + VERSION_STRING;
    public static final String PROGRAM_NAME_NO_SPACES = "SSH-Proxy-Switcher";
    public static final int APTIAPI_PROJECT_ID = 2;
    public static final int APTIAPI_VERSION_ID = 1;
    private static final File APPLICATION_DIR = new File(System.getenv("AppData"), "SSHProxySwitcher");
    private static String logoImageLocation = "/images/Logo-32px.png";
    private static BufferedImage logoImage = null;

    private static BufferedImage[] trayIconImages;

    static {
        try {
            logoImage = ImageIO.read(Versioning.class.getResource(logoImageLocation));
            trayIconImages = new BufferedImage[TrayIconImage.values().length];
            for (int i = 0; i < TrayIconImage.values().length; i++) {
                trayIconImages[i] = ImageIO.read(TrayIconImage.class.getResource(TrayIconImage.values()[i].getImagePath()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!APPLICATION_DIR.exists())
            APPLICATION_DIR.mkdir();
    }

    public static ImageIcon getLogoAsImageIcon() {
        return new ImageIcon(logoImage);
    }

    public static BufferedImage getLogoAsBufferedImage() {
        return logoImage;
    }

    public static BufferedImage getTrayIconImage(TrayIconImage image) {
        int index = 0;
        for (int i = 0; i < TrayIconImage.values().length; i++)
            if (TrayIconImage.values()[i].name().equals(image.name())) {
                index = i;
                break;
            }

        return trayIconImages[index];
    }

    public static Image getLogoAsJavaFXImage() {
        return new Image(Versioning.class.getResourceAsStream(logoImageLocation));
    }

    public static File getApplicationDir() {
        return APPLICATION_DIR;
    }

    @Override
    public double getVersion() {
        return VERSION;
    }

    @Override
    public String getVersionString() {
        return VERSION_STRING;
    }

    @Override
    public String getProgramName() {
        return PROGRAM_NAME;
    }

    @Override
    public String getProgramNameWithVersion() {
        return PROGRAM_NAME_WITH_VERSION;
    }

    @Override
    public int getAptiAPIProjectID() {
        return APTIAPI_PROJECT_ID;
    }

    @Override
    public int getAptiAPIVersionID() {
        return APTIAPI_VERSION_ID;
    }
}
