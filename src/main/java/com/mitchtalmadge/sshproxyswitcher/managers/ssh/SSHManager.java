package com.mitchtalmadge.sshproxyswitcher.managers.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.mitchtalmadge.sshproxyswitcher.SSHProxySwitcher;
import com.mitchtalmadge.sshproxyswitcher.managers.profiles.Profile;

import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class SSHManager {

    private SSHMaintainerThread thread;

    protected static Session createSessionFromProfile(Profile profile) throws JSchException {
        JSch jSch = new JSch();

        String sshHostName = profile.getSshHostName();
        int sshPort = profile.getSshHostPort() > 0 ? profile.getSshHostPort() : 22;
        String sshUsername = profile.getSshUsername();
        String sshPassword = profile.getSshPassword();
        String rsaKeyPath = profile.getSshRsaPrivateKeyFilePath();
        String rsaKeyPass = profile.getSshRsaPrivateKeyPassword();
        int proxyPort = profile.getProxyPort() > 0 ? profile.getProxyPort() : 2000;

        if (rsaKeyPath != null)
            if (!rsaKeyPass.isEmpty())
                jSch.addIdentity(rsaKeyPath, rsaKeyPass);
            else
                jSch.addIdentity(rsaKeyPath);

        Session session = jSch.getSession(sshUsername, sshHostName, sshPort);
        if (!sshPassword.isEmpty())
            session.setPassword(sshPassword);

        if (profile.shouldUseSshDynamicTunnel())
            session.setPortForwardingL(proxyPort, sshHostName, sshPort);

        return session;
    }

    public void startConnection(Profile profile) throws SSHConnectionException {
        SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Connecting to SSH Server for " + profile.getProfileName());
        stopConnection();
        Hashtable<String, String> config = new Hashtable<>();
        config.put("StrictHostKeyChecking", "no");
        JSch.setConfig(config);

        try {
            Session session = createSessionFromProfile(profile);
            session.connect(3000);

            thread = new SSHMaintainerThread(session, profile);
            thread.start();

            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Connected!");
        } catch (JSchException e) {
            throw new SSHConnectionException(e);
        }
    }

    public void stopConnection() {
        if (this.thread != null) {
            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Disconnecting from SSH...");
            thread.stopRunning();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread = null;
            SSHProxySwitcher.getInstance().getLoggingManager().log(Level.INFO, "Disconnected!");
        }
    }

    public boolean isConnected() {
        return this.thread != null && thread.isConnected();
    }

    private class SSHMaintainerThread extends Thread {

        private final AtomicBoolean running = new AtomicBoolean(true);
        private Profile profile;
        private Session session;

        public SSHMaintainerThread(Session session, Profile profile) {
            super("SSHMaintainerThread");
            this.session = session;
            this.profile = profile;
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
                    break;
                }
            }
            disconnectFromSession();
        }

        private void reconnectToSession() throws JSchException, InterruptedException {
            if (!session.isConnected()) {
                session = createSessionFromProfile(profile);
                session.connect(3000);
            }
        }

        private void disconnectFromSession() {
            if (session.isConnected())
                session.disconnect();
        }

        public boolean isConnected() {
            return session != null && session.isConnected();
        }
    }
}
