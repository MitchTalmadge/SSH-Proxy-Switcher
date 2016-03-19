package com.mitchtalmadge.sshproxyswitcher.managers.proxies;

import com.mitchtalmadge.sshproxyswitcher.managers.profiles.Profile;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

public class ProxyManager {

    private static final String SID_STRING = Advapi32Util.getAccountByName(Advapi32Util.getUserName()).sidString;
    private static final String REGISTRY_KEY_PATH = SID_STRING + "\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";

    private static final String PROXY_ENABLED_KEY = "ProxyEnable";
    private static final String PROXY_SETTINGS_KEY = "ProxyServer";

    public void setProxySettings(Profile profile) throws ProxySettingsException {
        if (profile == null) {
            setProxyEnabled(false);
            throw new ProxySettingsException("Profile is null");
        } else if (profile.getProxyPort() < 0 || profile.getProxyPort() > 65535) {
            setProxyEnabled(false);
            throw new ProxySettingsException("Profile is invalid");
        } else {
            setProxyEnabled(true);
            setProxy(profile.getProxyHostName(), profile.getProxyPort());
        }
    }

    private void setProxy(String proxyHostName, int proxyPort) {
        if (proxyHostName == null || proxyHostName.isEmpty())
            proxyHostName = "127.0.0.1";
        if (proxyPort == 0)
            proxyPort = 2000;
        Advapi32Util.registrySetStringValue(WinReg.HKEY_USERS, REGISTRY_KEY_PATH, PROXY_SETTINGS_KEY, "socks=" + proxyHostName + ":" + proxyPort);
    }

    public void disableProxySettings() {
        setProxyEnabled(false);
    }

    private void setProxyEnabled(boolean enabled) {
        Advapi32Util.registrySetIntValue(WinReg.HKEY_USERS, REGISTRY_KEY_PATH, PROXY_ENABLED_KEY, enabled ? 1 : 0);
    }
}
