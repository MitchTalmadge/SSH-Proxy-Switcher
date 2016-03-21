package com.mitchtalmadge.sshproxyswitcher.managers.ssh;

import com.mitchtalmadge.sshproxyswitcher.SSHProxySwitcher;
import com.mitchtalmadge.sshproxyswitcher.gui.TrayIconManager;
import com.mitchtalmadge.sshproxyswitcher.managers.profiles.Profile;
import com.sshtools.events.Event;
import com.sshtools.events.EventListener;
import com.sshtools.net.SocketTransport;
import com.sshtools.publickey.InvalidPassphraseException;
import com.sshtools.publickey.SshPrivateKeyFile;
import com.sshtools.publickey.SshPrivateKeyFileFactory;
import com.sshtools.ssh.*;
import com.sshtools.ssh.components.SshKeyPair;
import socks.ProxyServer;
import socks.server.ServerAuthenticatorNone;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class SSHManager {

    private SSHMaintainerThread sshMaintainerThread;
    private DynamicTunnelMaintainerThread dynamicTunnelMaintainerThread;

    public void startConnection(Profile profile) throws SSHConnectionException {
        SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Connecting to SSH Server for " + profile.getProfileName());
        stopConnection();

        try {
            SshConnector connector = SshConnector.createInstance();
            connector.getContext().setHostKeyVerification((s, sshPublicKey) -> true);

            SshClient sshClient = connectToSsh(connector, profile);

            sshMaintainerThread = new SSHMaintainerThread(sshClient, connector, profile);
            sshMaintainerThread.start();
        } catch (SSHConnectionException e) {
            stopConnection();
            throw e;
        } catch (Exception e) {
            stopConnection();
            throw new SSHConnectionException(e.getMessage());
        }
    }

    protected SshClient connectToSsh(SshConnector connector, Profile connectionProfile) throws SSHConnectionException {
        try {
            String sshHostName = connectionProfile.getSshHostName();
            int sshPort = connectionProfile.getSshHostPort() > 0 ? connectionProfile.getSshHostPort() : 22;
            String sshUsername = connectionProfile.getSshUsername();
            String sshPassword = connectionProfile.getSshPassword();
            String rsaKeyPath = connectionProfile.getSshRsaPrivateKeyFilePath();
            String rsaKeyPass = connectionProfile.getSshRsaPrivateKeyPassword();
            int proxyPort = connectionProfile.getProxyPort() > 0 ? connectionProfile.getProxyPort() : 2000;

            SshClient sshClient = connector.connect(new SocketTransport(sshHostName, sshPort), sshUsername, true);

            if (!rsaKeyPath.isEmpty()) {
                try {
                    tryAuthenticateWithRsa(new File(rsaKeyPath), rsaKeyPass, sshClient);
                } catch (SSHConnectionException e) {
                    try {
                        tryAuthenticateWithPassword(sshUsername, sshPassword, sshClient);
                    } catch (SSHConnectionException e1) {
                        throw new SSHConnectionException("Authentication Failed.");
                    }
                }
            } else {
                try {
                    tryAuthenticateWithPassword(sshUsername, sshPassword, sshClient);
                } catch (SSHConnectionException e1) {
                    throw new SSHConnectionException("Authentication Failed.");
                }
            }

            if (connectionProfile.shouldUseSshDynamicTunnel()) {
                SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Opening Dynamic Tunnel on localhost:" + proxyPort);
                dynamicTunnelMaintainerThread = new DynamicTunnelMaintainerThread(sshClient, proxyPort);
                dynamicTunnelMaintainerThread.start();
            }

            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Connected to " + sshHostName + ":" + sshPort);
            return sshClient;
        } catch (SshException | IOException e) {
            throw new SSHConnectionException(e.getMessage());
        }
    }

    private void tryAuthenticateWithRsa(File rsaKey, String rsaKeyPassword, SshClient sshClient) throws SSHConnectionException {
        try {
            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Authenticating with RSA Key...");
            PublicKeyAuthentication publicKeyAuthentication = new PublicKeyAuthentication();

            FileInputStream rsaKeyInputStream = new FileInputStream(rsaKey);
            SshPrivateKeyFile privateKeyFile = SshPrivateKeyFileFactory.parse(rsaKeyInputStream);
            rsaKeyInputStream.close();

            SshKeyPair keyPair = privateKeyFile.toKeyPair(rsaKeyPassword);

            publicKeyAuthentication.setPrivateKey(keyPair.getPrivateKey());
            publicKeyAuthentication.setPublicKey(keyPair.getPublicKey());

            if (sshClient.authenticate(publicKeyAuthentication) != SshAuthentication.COMPLETE) {
                SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "RSA Key Auth Failed.");
                throw new SSHConnectionException("RSA Key Auth Failed.");
            }
        } catch (InvalidPassphraseException | IOException | SshException e) {
            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "RSA Key Auth Failed: ");
            e.printStackTrace();
            throw new SSHConnectionException(e.getMessage());
        }
    }

    private void tryAuthenticateWithPassword(String username, String password, SshClient sshClient) throws SSHConnectionException {
        try {
            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Authenticating with Username and Password...");
            PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
            passwordAuthentication.setUsername(username);
            passwordAuthentication.setPassword(password);

            if (sshClient.authenticate(passwordAuthentication) != SshAuthentication.COMPLETE) {
                SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Password Auth Failed.");
                throw new SSHConnectionException("Password Auth Failed.");
            }
        } catch (SshException e) {
            throw new SSHConnectionException(e.getMessage());
        }
    }

    public void stopConnection() {
        shutDownDynamicTunnel();
        if (this.sshMaintainerThread != null) {
            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Disconnecting from SSH...");
            sshMaintainerThread.stopRunning();
            try {
                sshMaintainerThread.join();
            } catch (InterruptedException e) {
            }
            sshMaintainerThread = null;
            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Disconnected.");
        }
    }

    protected void shutDownDynamicTunnel() {
        if (this.dynamicTunnelMaintainerThread != null) {
            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Closing Dynamic Tunnel...");
            dynamicTunnelMaintainerThread.shutDown();
            try {
                dynamicTunnelMaintainerThread.join();
            } catch (InterruptedException e) {
            }
            dynamicTunnelMaintainerThread = null;
            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Dynamic Tunnel Closed.");
        }
    }

    public boolean isConnected() {
        return this.sshMaintainerThread != null && sshMaintainerThread.isConnected();
    }

    private class SSHMaintainerThread extends Thread {

        private final AtomicBoolean running = new AtomicBoolean(true);
        private SshClient sshClient;
        private SshConnector connector;
        private Profile connectionProfile;

        public SSHMaintainerThread(SshClient sshClient, SshConnector connector, Profile connectionProfile) {
            super("SSHMaintainerThread");
            this.sshClient = sshClient;
            this.connector = connector;
            this.connectionProfile = connectionProfile;
        }

        public void stopRunning() {
            this.running.set(false);
        }

        @Override
        public void run() {
            while (running.get()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
                try {
                    reconnectToSession();
                } catch (Exception e) {
                    SSHProxySwitcher.getInstance().getTrayIconManager().setStatus(TrayIconManager.STATUS_ERROR);
                    e.printStackTrace();
                    break;
                }
            }
            disconnectFromSshClient();
        }

        private void reconnectToSession() throws InterruptedException {
            if (!sshClient.isConnected()) {
                SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "SSH Connection Lost! Reconnecting...");
                SSHProxySwitcher.getInstance().getTrayIconManager().setStatus(TrayIconManager.STATUS_CONNECTING);
                try {
                    shutDownDynamicTunnel();
                    sshClient = connectToSsh(connector, connectionProfile);
                    SSHProxySwitcher.getInstance().getTrayIconManager().setStatus(TrayIconManager.STATUS_CONNECTED);
                } catch (SSHConnectionException e) {
                    SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Error While Reconnecting: " + e.getMessage());
                    SSHProxySwitcher.getInstance().getTrayIconManager().setStatus(TrayIconManager.STATUS_ERROR);
                }
            }
        }

        private void disconnectFromSshClient() {
            if (sshClient.isConnected())
                sshClient.disconnect();
        }

        public boolean isConnected() {
            return sshClient != null && sshClient.isConnected();
        }
    }

    private class DynamicTunnelMaintainerThread extends Thread {

        private SshClient sshClient;
        private int proxyPort;
        private QuietProxyServer socksServer;

        public DynamicTunnelMaintainerThread(SshClient sshClient, int proxyPort) {
            super("DynamicTunnelMaintainerThread");
            this.sshClient = sshClient;
            this.proxyPort = proxyPort;
        }

        @Override
        public void run() {
            socksServer = new QuietProxyServer(new ServerAuthenticatorNone(), sshClient);
            socksServer.start(proxyPort);
        }

        public void shutDown() {
            if (socksServer != null) {
                socksServer.stop();
            }
            this.interrupt();
        }
    }
}
