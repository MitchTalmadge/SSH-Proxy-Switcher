package net.liveforcode.SSHProxySwitcher.Managers.SSHManager;

import net.liveforcode.SSHProxySwitcher.Profile;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.UserAuth;
import org.apache.sshd.server.auth.UserAuthPassword;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.*;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SSHManagerTest {

    public static final String CORRECT_HOST = "localhost";
    public static final String INCORRECT_HOST = "incorrect.host";
    public static final int CORRECT_HOST_PORT = 28;
    public static final String USERNAME = "tester";
    public static final String CORRECT_PASSWORD = "correctPassword";
    public static final String INCORRECT_PASSWORD = "incorrectPassword";

    private static Profile validProfile;
    private static Profile invalidAuthProfile;
    private static Profile invalidHostProfile;
    private static SshServer sshd;

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    private SSHManager sshManager;

    @BeforeClass
    public static void beforeClass() throws IOException {
        validProfile = new Profile();
        validProfile.setProfileName("Valid Profile");
        validProfile.setSshHostAddress(CORRECT_HOST);
        validProfile.setSshHostPort(CORRECT_HOST_PORT);
        validProfile.setSshProxyPort(2000);
        validProfile.setSshUsername(USERNAME);
        validProfile.setSshPassword(CORRECT_PASSWORD);

        invalidAuthProfile = new Profile(validProfile);
        invalidAuthProfile.setProfileName("Invalid Profile");
        invalidAuthProfile.setSshPassword(INCORRECT_PASSWORD);

        invalidHostProfile = new Profile(validProfile);
        invalidHostProfile.setProfileName("Invalid Host Profile");
        invalidHostProfile.setSshHostAddress(INCORRECT_HOST);

        sshd = createSshServer();
        sshd.start();
    }

    @AfterClass
    public static void afterClass() throws InterruptedException {
        sshd.stop();
    }

    public static SshServer createSshServer() {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setHost(CORRECT_HOST);
        sshd.setPort(CORRECT_HOST_PORT);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("/hostkey.ser"));

        final List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<>();
        userAuthFactories.add(new UserAuthPassword.Factory());
        sshd.setUserAuthFactories(userAuthFactories);

        sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
            @Override
            public boolean authenticate(String username, String password, ServerSession serverSession) {
                return username.equals(USERNAME) && password.equals(CORRECT_PASSWORD);
            }
        });

        sshd.setCommandFactory(new ScpCommandFactory());

        List<NamedFactory<Command>> namedFactoryList = new ArrayList<>();
        namedFactoryList.add(new SftpSubsystem.Factory());
        sshd.setSubsystemFactories(namedFactoryList);
        return sshd;
    }

    @Before
    public void setUp() throws Exception {
        this.sshManager = new SSHManager();
        assertNotNull("SSH Manager is null", sshManager);
    }

    @After
    public void tearDown() throws Exception {
        this.sshManager.stopConnection();
    }

    @Test
    public void testStartConnectionWillOpenConnectionIfEverythingValid() throws Exception {
        sshManager.startConnection(validProfile);
        assertTrue("Connection is not established", sshManager.isConnected());
    }

    @Test
    public void testStartConnectionWillThrowAuthFailExceptionIfInvalidAuth() throws Exception {
        exception.expectMessage("Auth fail");
        sshManager.startConnection(invalidAuthProfile);
    }

    @Test
    public void testStartConnectionWillThrowUnknownExceptionIfUnknownHost() throws Exception {
        exception.expectMessage("UnknownHostException");
        sshManager.startConnection(invalidHostProfile);
    }

    @Test
    public void testSSHManagerWillRestartConnectionIfClosedUnexpectedly() throws Exception {
        sshManager.startConnection(validProfile);
        assertTrue("Connection is not established", sshManager.isConnected());

        assertEquals("Incorrect number of active sessions", 1, sshd.getActiveSessions().size());
        Thread.sleep(100); //Give it time before disconnection
        sshd.getActiveSessions().get(0).disconnect(0, "Testing Unexpected Close");
        Thread.sleep(50); //Give it time to disconnect
        assertFalse("Connection is still established", sshManager.isConnected());
        Thread.sleep(1000);
        assertTrue("Connection is not established", sshManager.isConnected());
    }
}