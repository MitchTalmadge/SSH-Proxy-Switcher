package com.mitchtalmadge.sshproxyswitcher.Managers2.ProxyManager2;

import com.mitchtalmadge.sshproxyswitcher.ExampleProfileFactory;
import com.mitchtalmadge.sshproxyswitcher.Managers2.ProfileManager2.Profile;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProxyManagerTest {

    private final static String SID_STRING = Advapi32Util.getAccountByName(Advapi32Util.getUserName()).sidString;
    private final static String REGISTRY_KEY = SID_STRING + "\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private ProxyManager proxyManager;

    @Before
    public void setUp() throws Exception {
        this.proxyManager = new ProxyManager();
        assertNotNull("ProxyManager is null", proxyManager);
        this.proxyManager.disableProxySettings();
    }

    @After
    public void tearDown() throws Exception {
        this.proxyManager.disableProxySettings();
    }

    @Test
    public void testSetProxySettingsWillCorrectlySetProxySettingsIfValidProfile() throws Exception {
        Profile profile = ExampleProfileFactory.getValidProfile();
        this.proxyManager.setProxySettings(profile);
        assertProxyEnabled(true);
        assertProxySettingsCorrect(profile);
    }

    @Test
    public void testSetProxySettingsWillResetProxySettingsIfNull() throws Exception {
        expectedException.expect(ProxySettingsException.class);
        expectedException.expectMessage("Profile is null");
        this.proxyManager.setProxySettings(null);
        assertProxyEnabled(false);
    }

    @Test
    public void testSetProxySettingsWillResetProxySettingsIfInvalidProfile() throws Exception {
        expectedException.expect(ProxySettingsException.class);
        expectedException.expectMessage("Profile is invalid");
        this.proxyManager.setProxySettings(ExampleProfileFactory.getInvalidProxyProfile());
        assertProxyEnabled(false);
    }

    @Test
    public void testDisableProxyWillDisableProxy() throws Exception {
        this.proxyManager.setProxySettings(ExampleProfileFactory.getValidProfile());
        assertProxyEnabled(true);
        this.proxyManager.disableProxySettings();
        assertProxyEnabled(false);
    }

    private void assertProxyEnabled(boolean expected) {
        assertEquals("ProxyEnable Registry Value Incorrect", expected ? 1 : 0, Advapi32Util.registryGetIntValue(WinReg.HKEY_USERS, REGISTRY_KEY, "ProxyEnable"));
    }

    private void assertProxySettingsCorrect(Profile profile) {
        String correctSettings = "socks=localhost:" + profile.getSshProxyPort();
        String currentSettings = Advapi32Util.registryGetStringValue(WinReg.HKEY_USERS, REGISTRY_KEY, "ProxyServer");
        assertEquals("Proxy Settings are Incorrect", correctSettings, currentSettings);
    }
}