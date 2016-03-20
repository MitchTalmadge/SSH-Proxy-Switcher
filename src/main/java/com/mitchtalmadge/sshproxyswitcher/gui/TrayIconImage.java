package com.mitchtalmadge.sshproxyswitcher.gui;

public enum TrayIconImage {

    DEFAULT("/images/Logo-16px.png"),
    RED("/images/Logo-16px-Red.png"),
    YELLOW("/images/Logo-16px-Yellow.png"),
    GREEN("/images/Logo-16px-Green.png");

    private String imagePath;

    TrayIconImage(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }
}
