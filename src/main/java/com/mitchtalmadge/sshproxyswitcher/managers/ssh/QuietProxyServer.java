package com.mitchtalmadge.sshproxyswitcher.managers.ssh;

import com.sshtools.ssh.SshClient;
import com.sun.corba.se.spi.activation.Server;
import socks.ProxyServer;
import socks.server.ServerAuthenticator;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class QuietProxyServer extends ProxyServer {

    public QuietProxyServer(ServerAuthenticator auth, SshClient agent) {
        super(auth, agent);
    }

    @Override
    public void start(int port, int backlog, InetAddress localIP) {
        try {
            Field ssField = ProxyServer.class.getDeclaredField("ss");
            ssField.setAccessible(true);
            Field authField = ProxyServer.class.getDeclaredField("auth");
            authField.setAccessible(true);
            Field agentField = ProxyServer.class.getDeclaredField("agent");
            agentField.setAccessible(true);

            ssField.set(this, new ServerSocket(port, backlog, localIP));
            //log("Starting SOCKS Proxy on:" + this.ss.getInetAddress().getHostAddress() + ":" + this.ss.getLocalPort());

            while (true) {
                try {
                    Socket ioe = ((ServerSocket) ssField.get(this)).accept();
                    //log("Accepted from:" + ioe.getInetAddress().getHostName() + ":" + ioe.getPort());
                    Constructor<ProxyServer> constructor = ProxyServer.class.getDeclaredConstructor(ServerAuthenticator.class, Socket.class, SshClient.class);
                    constructor.setAccessible(true);
                    ProxyServer ps = constructor.newInstance(authField.get(this), ioe, agentField.get(this));
                    (new Thread(ps)).start();
                } catch (SocketException e) {
                    break;
                }
            }
        } catch (IOException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException | InstantiationException var9) {
            var9.printStackTrace();
        }
    }
}
