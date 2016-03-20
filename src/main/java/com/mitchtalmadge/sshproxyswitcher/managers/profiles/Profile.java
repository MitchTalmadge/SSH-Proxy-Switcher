package com.mitchtalmadge.sshproxyswitcher.managers.profiles;

import java.io.Serializable;

public class Profile implements Serializable {
    private String profileName;
    private boolean connectToSsh;
    private String sshHostName;
    private int sshHostPort;
    private String sshUsername;
    private String sshPassword;
    private String sshRsaPrivateKeyFilePath;
    private String sshRsaPrivateKeyPassword;
    private boolean autoEnableProxy;
    private boolean useSshDynamicTunnel;
    private String proxyHostName;
    private int proxyPort;

    public Profile(String name) {
        profileName = name;
    }

    public Profile(Profile otherProfile) {
        profileName = otherProfile.getProfileName();
        connectToSsh = otherProfile.shouldConnectToSsh();
        sshHostName = otherProfile.getSshHostName();
        sshHostPort = otherProfile.getSshHostPort();
        sshUsername = otherProfile.getSshUsername();
        sshPassword = otherProfile.getSshPassword();
        sshRsaPrivateKeyFilePath = otherProfile.getSshRsaPrivateKeyFilePath();
        sshRsaPrivateKeyPassword = otherProfile.getSshRsaPrivateKeyPassword();
        autoEnableProxy = otherProfile.shouldAutoEnableProxy();
        useSshDynamicTunnel = otherProfile.shouldUseSshDynamicTunnel();
        proxyHostName = otherProfile.getProxyHostName();
        proxyPort = otherProfile.getProxyPort();
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public boolean shouldConnectToSsh() {
        return connectToSsh;
    }

    public void setConnectToSsh(boolean connectToSsh) {
        this.connectToSsh = connectToSsh;
    }

    public String getSshHostName() {
        return sshHostName;
    }

    public void setSshHostName(String sshHostName) {
        this.sshHostName = sshHostName;
    }

    public int getSshHostPort() {
        return sshHostPort;
    }

    public void setSshHostPort(int sshHostPort) {
        this.sshHostPort = sshHostPort;
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

    public String getSshRsaPrivateKeyFilePath() {
        return sshRsaPrivateKeyFilePath;
    }

    public void setSshRsaPrivateKeyFilePath(String sshRsaPrivateKeyFilePath) {
        this.sshRsaPrivateKeyFilePath = sshRsaPrivateKeyFilePath;
    }

    public String getSshRsaPrivateKeyPassword() {
        return sshRsaPrivateKeyPassword;
    }

    public void setSshRsaPrivateKeyPassword(String sshRsaPrivateKeyPassword) {
        this.sshRsaPrivateKeyPassword = sshRsaPrivateKeyPassword;
    }

    public boolean shouldAutoEnableProxy() {
        return autoEnableProxy;
    }

    public void setAutoEnableProxy(boolean autoEnableProxy) {
        this.autoEnableProxy = autoEnableProxy;
    }

    public boolean shouldUseSshDynamicTunnel() {
        return useSshDynamicTunnel;
    }

    public void setUseSshDynamicTunnel(boolean useSshDynamicTunnel) {
        this.useSshDynamicTunnel = useSshDynamicTunnel;
    }

    public String getProxyHostName() {
        return proxyHostName;
    }

    public void setProxyHostName(String proxyHostName) {
        this.proxyHostName = proxyHostName;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    @Override
    public String toString() {
        return profileName;
    }
}
