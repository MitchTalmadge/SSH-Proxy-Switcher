package com.mitchtalmadge.sshproxyswitcher.managers.proxies;

import com.mitchtalmadge.sshproxyswitcher.SSHProxySwitcher;
import com.mitchtalmadge.sshproxyswitcher.managers.profiles.Profile;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import java.util.logging.Level;

public class ProxyManager {

    private static final String SID_STRING = Advapi32Util.getAccountByName(Advapi32Util.getUserName()).sidString;
    private static final String REGISTRY_KEY_PATH = SID_STRING + "\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";

    private static final String PROXY_ENABLED_KEY = "ProxyEnable";
    private static final String PROXY_SETTINGS_KEY = "ProxyServer";

    private final int originalProxyEnabled;
    private final String originalProxySettings;

    public ProxyManager() {
        originalProxyEnabled = getProxyEnabledValue();
        originalProxySettings = getProxySettingsValue();
    }

    public void setProxySettings(Profile profile) throws ProxySettingsException {
        if (profile != null) {
            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Checking Proxy Settings for " + profile.getProfileName() + "...");
            if (profile.getProxyPort() < 0 || profile.getProxyPort() > 65535) {
                SSHProxySwitcher.getInstance().getLoggingManager().log(Level.SEVERE, "Port out of bounds: " + profile.getProxyPort());
                setProxyEnabled(false);
                throw new ProxySettingsException("Port is out of bounds: " + profile.getProxyPort());
            } else {
                setProxy(profile.getProxyHostName(), profile.getProxyPort(), profile.shouldUseSshDynamicTunnel());
                setProxyEnabled(true);
            }
        } else {
            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Tried to set Proxy Settings, but profile was null.");
            setProxyEnabled(false);
            throw new ProxySettingsException("Profile is null");
        }
    }

    private void setProxy(String proxyHostName, int proxyPort, boolean socks) {
        if (proxyHostName == null || proxyHostName.isEmpty())
            proxyHostName = "localhost";
        if (proxyPort == 0)
            proxyPort = 2000;
        setProxySettingsValue((socks ? "socks=" : "") + proxyHostName + ":" + proxyPort);
        SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Proxy Settings Updated.");
    }

    public void disableProxySettings() {
        setProxyEnabledValue(originalProxyEnabled);
        setProxySettingsValue(originalProxySettings);
    }

    private void setProxyEnabled(boolean enabled) {
        SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Proxy " + (enabled ? "Enabled." : "Disabled."));
        setProxyEnabledValue(enabled ? 1 : 0);
    }

    private int getProxyEnabledValue() {
        return Advapi32Util.registryGetIntValue(WinReg.HKEY_USERS, REGISTRY_KEY_PATH, PROXY_ENABLED_KEY);
    }

    private void setProxyEnabledValue(int value) {
        Advapi32Util.registrySetIntValue(WinReg.HKEY_USERS, REGISTRY_KEY_PATH, PROXY_ENABLED_KEY, value);
    }

    private String getProxySettingsValue() {
        return Advapi32Util.registryGetStringValue(WinReg.HKEY_USERS, REGISTRY_KEY_PATH, PROXY_SETTINGS_KEY);
    }

    private void setProxySettingsValue(String value) {
        Advapi32Util.registrySetStringValue(WinReg.HKEY_USERS, REGISTRY_KEY_PATH, PROXY_SETTINGS_KEY, value);
    }
}
