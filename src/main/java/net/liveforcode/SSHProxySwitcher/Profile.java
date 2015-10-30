package net.liveforcode.SSHProxySwitcher;

import java.io.File;

public class Profile {
    private String profileName;
    private String sshHostAddress;
    private int sshHostPort;
    private int sshProxyPort;
    private String sshUsername;
    private String sshPassword;
    private File sshPrivateKey;

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getSshHostAddress() {
        return sshHostAddress;
    }

    public void setSshHostAddress(String sshHostAddress) {
        this.sshHostAddress = sshHostAddress;
    }

    public int getSshHostPort() {
        return sshHostPort;
    }

    public void setSshHostPort(int sshHostPort) {
        this.sshHostPort = sshHostPort;
    }

    public int getSshProxyPort() {
        return sshProxyPort;
    }

    public void setSshProxyPort(int sshProxyPort) {
        this.sshProxyPort = sshProxyPort;
    }

    public String getSshUsername() {
        return sshUsername;
    }

    public void setSshUsername(String sshUsername) {
        this.sshUsername = sshUsername;
    }

    public String getSshPassword() {
        return sshPassword;
    }

    public void setSshPassword(String sshPassword) {
        this.sshPassword = sshPassword;
    }

    public File getSshPrivateKey() {
        return sshPrivateKey;
    }

    public void setSshPrivateKey(File sshPrivateKey) {
        this.sshPrivateKey = sshPrivateKey;
    }
}
