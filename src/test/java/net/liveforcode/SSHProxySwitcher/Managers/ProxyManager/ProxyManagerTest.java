package net.liveforcode.SSHProxySwitcher.Managers.ProxyManager;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import net.liveforcode.SSHProxySwitcher.ExampleProfileFactory;
import net.liveforcode.SSHProxySwitcher.Managers.ProfileManager.Profile;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProxyManagerTest {

    private final static String SID_STRING = Advapi32Util.getAccountByName(Advapi32Util.getUserName()).sidString;
    private final static String REGISTRY_KEY = SID_STRING + "\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";
    private ProxyManager proxyManager;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
        profile.setSshProxyPort((int)(2000 * Math.random()));
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