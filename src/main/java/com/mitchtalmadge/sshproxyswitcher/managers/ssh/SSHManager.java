package com.mitchtalmadge.sshproxyswitcher.managers.ssh;

import com.mitchtalmadge.sshproxyswitcher.SSHProxySwitcher;
import com.mitchtalmadge.sshproxyswitcher.gui.TrayIconManager;
import com.mitchtalmadge.sshproxyswitcher.managers.profiles.Profile;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.ConnectionMonitor;
import com.trilead.ssh2.DynamicPortForwarder;
import com.trilead.ssh2.ServerHostKeyVerifier;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Level;

public class SSHManager implements ConnectionMonitor {

    private Connection connection;
    private Profile connectionProfile;
    private DynamicPortForwarder dynamicPortForwarder;

    public void startConnection(Profile profile) throws SSHConnectionException {
        SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Connecting to SSH Server for " + profile.getProfileName());
        if (connection != null)
            stopConnection();

        connectionProfile = profile;

        String sshHostName = profile.getSshHostName();
        int sshPort = profile.getSshHostPort() > 0 ? profile.getSshHostPort() : 22;
        String sshUsername = profile.getSshUsername();
        String sshPassword = profile.getSshPassword();
        String rsaKeyPath = profile.getSshRsaPrivateKeyFilePath();
        String rsaKeyPass = profile.getSshRsaPrivateKeyPassword();
        int proxyPort = profile.getProxyPort() > 0 ? profile.getProxyPort() : 2000;

        this.connection = new Connection(sshHostName, sshPort);
        connection.addConnectionMonitor(this);

        try {
            connection.connect(new IgnoreHostKeyVerifier());
        } catch (IOException e) {
            stopConnection();
            SSHProxySwitcher.getInstance().getTrayIconManager().setStatus(TrayIconManager.STATUS_ERROR);
            throw new SSHConnectionException(e.getMessage());
        }

        try {
            if (!rsaKeyPath.isEmpty()) {
                if (!connection.isAuthenticationComplete()) {
                    try {
                        SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Authenticating with RSA Key...");
                        connection.authenticateWithPublicKey(sshUsername, new File(rsaKeyPath), rsaKeyPass);
                    } catch (IOException ignored) {
                    }
                }

                if (!connection.isAuthenticationComplete()) {
                    SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "RSA Key Auth Failed. Trying Username and Password...");
                    connection.authenticateWithPassword(sshUsername, sshPassword);
                }

                if (!connection.isAuthenticationComplete()) {
                    SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "All Auth Methods Failed.");
                    stopConnection();
                    throw new SSHConnectionException("All Auth Methods Failed.");
                }
            } else {
                if (!connection.isAuthenticationComplete()) {
                    SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Authenticating with Username and Password...");
                    connection.authenticateWithPassword(sshUsername, sshPassword);
                }

                if (!connection.isAuthenticationComplete()) {
                    SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "All Auth Methods Failed.");
                    stopConnection();
                    throw new SSHConnectionException("All Auth Methods Failed.");
                }
            }
        } catch (IOException e) {
            stopConnection();
            SSHProxySwitcher.getInstance().getTrayIconManager().setStatus(TrayIconManager.STATUS_ERROR);
            throw new SSHConnectionException(e.getMessage());
        }

        if (profile.shouldUseSshDynamicTunnel()) {
            try {
                SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Opening Dynamic Tunnel on localhost:" + proxyPort + ".");
                this.dynamicPortForwarder = connection.createDynamicPortForwarder(new InetSocketAddress(InetAddress.getLocalHost(), proxyPort));
            } catch (IOException e) {
                stopConnection();
                SSHProxySwitcher.getInstance().getTrayIconManager().setStatus(TrayIconManager.STATUS_ERROR);
                throw new SSHConnectionException(e.getMessage());
            }
        }

        SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Connected!");
    }

    public void stopConnection() {
        SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Disconnecting from SSH...");
        if (this.connection != null) {
            if (this.dynamicPortForwarder != null) {
                SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Closing Dynamic Tunnel...");
                try {
                    this.dynamicPortForwarder.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Closing SSH Connection...");
            connection.close();
            connection = null;
            connectionProfile = null;
            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Disconnected!");
        }
    }

    public boolean isConnected() {
        return this.connection != null && this.connection.isConnected();
    }

    @Override
    public void connectionLost(Throwable reason) {
        SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "SSH Connection Lost! Reason: " + reason.getMessage());
        if (!reason.getMessage().equals("Closed due to user request.") && !reason.getMessage().equals("There was a problem during connect.")) {
            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Reconnecting...");
            try {
                SSHProxySwitcher.getInstance().getTrayIconManager().setStatus(TrayIconManager.STATUS_CONNECTING);
                startConnection(connectionProfile);
                SSHProxySwitcher.getInstance().getTrayIconManager().setStatus(TrayIconManager.STATUS_CONNECTED);
            } catch (Exception e) {
                SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Could not Reconnect!");
                SSHProxySwitcher.getInstance().getTrayIconManager().setStatus(TrayIconManager.STATUS_ERROR);
                e.printStackTrace();
            }
        }
    }

    private class IgnoreHostKeyVerifier implements ServerHostKeyVerifier {

        @Override
        public boolean verifyServerHostKey(String hostname, int port, String serverHostKeyAlgorithm, byte[] serverHostKey) throws Exception {
            return true;
        }
    }
}
