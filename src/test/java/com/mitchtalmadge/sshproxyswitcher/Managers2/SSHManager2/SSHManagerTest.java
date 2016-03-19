package com.mitchtalmadge.sshproxyswitcher.Managers2.SSHManager2;

import com.mitchtalmadge.sshproxyswitcher.ExampleProfileFactory;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.UserAuth;
import org.apache.sshd.server.auth.UserAuthPasswordFactory;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SSHManagerTest {

    private static SshServer sshServer;

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    private SSHManager sshManager;

    @BeforeClass
    public static void beforeClass() throws IOException, URISyntaxException {
        sshServer = createSshServer();
        sshServer.start();
    }

    @AfterClass
    public static void afterClass() throws InterruptedException, IOException {
        sshServer.stop();
    }

    public static SshServer createSshServer() throws URISyntaxException {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setHost(ExampleProfileFactory.CORRECT_HOST);
        sshd.setPort(ExampleProfileFactory.CORRECT_HOST_PORT);
        SimpleGeneratorHostKeyProvider keyProvider = new SimpleGeneratorHostKeyProvider();
        sshd.setKeyPairProvider(keyProvider);
        keyProvider.setFile(new File(SSHManagerTest.class.getResource("/hostkey.ser").toURI()));

        final List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<>();
        userAuthFactories.add(new UserAuthPasswordFactory());
        sshd.setUserAuthFactories(userAuthFactories);

        sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
            @Override
            public boolean authenticate(String username, String password, ServerSession serverSession) {
                return username.equals(ExampleProfileFactory.USERNAME) && password.equals(ExampleProfileFactory.CORRECT_PASSWORD);
            }
        });

        sshd.setCommandFactory(new ScpCommandFactory());

        List<NamedFactory<Command>> namedFactoryList = new ArrayList<>();
        namedFactoryList.add(new SftpSubsystemFactory());
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
        connectWithValidProfile();
    }

    @Test
    public void testStartConnectionWillThrowAuthFailExceptionIfInvalidAuth() throws Exception {
        exception.expectMessage("Auth fail");
        sshManager.startConnection(ExampleProfileFactory.getInvalidAuthProfile());
    }

    @Test
    public void testStartConnectionWillThrowUnknownExceptionIfUnknownHost() throws Exception {
        exception.expectMessage("UnknownHostException");
        sshManager.startConnection(ExampleProfileFactory.getInvalidHostProfile());
    }

    @Test
    public void testSSHManagerWillRestartConnectionIfClosedUnexpectedly() throws Exception {
        connectWithValidProfile();

        assertEquals("Incorrect number of active sessions", 1, sshServer.getActiveSessions().size());
        Thread.sleep(100); //Give it time before disconnection
        sshServer.getActiveSessions().get(0).disconnect(0, "Testing Unexpected Close");
        Thread.sleep(100); //Give it time to disconnect
        assertFalse("Connection is still established", sshManager.isConnected());
        Thread.sleep(500); //Give it time to reconnect
        assertTrue("Connection is not established", sshManager.isConnected());
    }

    private void connectWithValidProfile() throws SSHConnectionException {
        sshManager.startConnection(ExampleProfileFactory.getValidProfile());
        assertTrue("Connection is not established", sshManager.isConnected());
    }

    @Test
    public void testStopConnectionWillDisconnectSession() throws Exception {
        connectWithValidProfile();

        sshManager.stopConnection();
        assertFalse("Connection is still established", sshManager.isConnected());
    }
}