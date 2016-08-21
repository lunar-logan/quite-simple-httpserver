package org.qshs;

import org.qshs.util.LogUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * @author Anurag
 */
public class QuiteSmallHttpServer {
    private static final Logger L = LogUtil.getLogger(QuiteSmallHttpServer.class.getName());

    private final int port;
    private final ServerSocket serverSocket;
    private final RequestHandlerService handlerService;
    private volatile boolean stop;

    public QuiteSmallHttpServer(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(this.port);
        this.stop = false;
        this.handlerService = new RequestHandlerService();
    }

    public void start() {
        L.info("[listening on] " + serverSocket.getInetAddress());
        try {
            while (!stop) {
                Socket client = serverSocket.accept();
                L.info("[connected to] " + client);
                handlerService.handle(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new QuiteSmallHttpServer(4567).start();
    }
}
