package com.mitchtalmadge.sshproxyswitcher.managers.ssh;

import com.jcraft.jsch.IdentityRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.mitchtalmadge.sshproxyswitcher.managers.profiles.Profile;

import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;

public class SSHManager {

    private SSHMaintainerThread thread;

    protected static Session createSessionFromProfile(Profile profile) throws JSchException {
        JSch jSch = new JSch();

        if (profile.getSshRsaPrivateKeyFile() != null)
            jSch.addIdentity(profile.getSshRsaPrivateKeyFile().getAbsolutePath());

        Session session = jSch.getSession(profile.getSshUsername(), profile.getSshHostName(), profile.getSshHostPort());
        session.setPassword(profile.getSshPassword());
        session.setPortForwardingL(profile.getProxyPort(), profile.getSshHostName(), profile.getSshHostPort());

        return session;
    }

    public void startConnection(Profile profile) throws SSHConnectionException {
        stopConnection();
        Hashtable<String, String> config = new Hashtable<>();
        config.put("StrictHostKeyChecking", "no");
        JSch.setConfig(config);

        try {
            Session session = createSessionFromProfile(profile);
            session.connect(3000);

            thread = new SSHMaintainerThread(session, profile);
            thread.start();
        } catch (JSchException e) {
            throw new SSHConnectionException(e);
        }
    }

    public void stopConnection() {
        if (this.thread != null) {
            thread.stopRunning();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread = null;
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
