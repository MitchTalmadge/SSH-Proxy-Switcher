package net.liveforcode.SSHProxySwitcher.Managers.ProxyManager;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import net.liveforcode.SSHProxySwitcher.Managers.ProfileManager.Profile;

public class ProxyManager {

    private final String SID_STRING = Advapi32Util.getAccountByName(Advapi32Util.getUserName()).sidString;
    private final String REGISTRY_KEY_PATH = SID_STRING + "\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";

    private final String PROXY_ENABLED_KEY = "ProxyEnable";
    private final String PROXY_SETTINGS_KEY = "ProxyServer";

    public void setProxySettings(Profile profile) throws ProxySettingsException {
        if (profile == null)
        {
            setProxyEnabled(false);
            throw new ProxySettingsException("Profile is null");
        }
        else if(profile.getSshProxyPort() < 0 || profile.getSshProxyPort() > 65535) {
            setProxyEnabled(false);
            throw new ProxySettingsException("Profile is invalid");
        } else {
            setProxyEnabled(true);
            setProxyPort(profile.getSshProxyPort());
        }
    }

    private void setProxyPort(int sshProxyPort) {
        Advapi32Util.registrySetStringValue(WinReg.HKEY_USERS, REGISTRY_KEY_PATH, PROXY_SETTINGS_KEY, "socks=localhost:" + sshProxyPort);
    }

    public void disableProxySettings() {
        setProxyEnabled(false);
    }

    private void setProxyEnabled(boolean enabled) {
        Advapi32Util.registrySetIntValue(WinReg.HKEY_USERS, REGISTRY_KEY_PATH, PROXY_ENABLED_KEY, enabled ? 1 : 0);
    }
}
