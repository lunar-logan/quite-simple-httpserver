package org.qshs;

import org.qshs.spi.Response;
import org.qshs.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * @author Anurag
 */
public class RequestHandlerService {
    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private static final int THREADS_LIMIT = 100;
    private static final Logger L = LogUtil.getLogger(RequestHandlerService.class.getName());

    private final int numThreads;
    private final ExecutorService threadPool;
    private final boolean verbose;

    public RequestHandlerService(int numThreads, boolean verbose) {
        if (numThreads <= 0 || numThreads > THREADS_LIMIT) {
            throw new IllegalArgumentException();
        }
        this.numThreads = numThreads;
        this.verbose = verbose;
        this.threadPool = Executors.newFixedThreadPool(this.numThreads);
        L.info("Thread pool initiated with " + this.numThreads + " fixed thread(s)");
    }

    public RequestHandlerService(boolean verbose) {
        this(NUM_THREADS, verbose);
    }

    public RequestHandlerService() {
        this(NUM_THREADS, false);
    }

    public void handle(Socket client) {
        try {
            threadPool.execute(new RequestHandler(client));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        threadPool.shutdown();
    }

    public void kill() {
        threadPool.shutdownNow();
    }

    static class RequestHandler implements Runnable {

        private final Socket socket;
        private final InputStream ins;
        private final OutputStream outs;

        public RequestHandler(Socket clientSocket) throws IOException {
            this.socket = clientSocket;
            this.ins = clientSocket.getInputStream();
            this.outs = clientSocket.getOutputStream();
        }

        @Override
        public void run() {
            HttpRequestStream requestStream = new HttpRequestStream(ins);
            requestStream.setOnRequest((req) -> {
                L.info("[parsed request] " + req);
                Response res = new HttpResponse(req);
                L.info("[parsed response] " + res.getRawResponse());
                try {

                    Path path = res.getFilePath();
                    if (path.toFile().isDirectory()) {
                        Path rootPath = Paths.get(System.getProperty("user.dir"), "httpRoot");

                        StringBuilder response = new StringBuilder();
                        response.append("<html>")
                                .append("<head></head>")
                                .append("<body>")
                                .append("<h2>Directory Contents</h2>")
                                .append("<ul>");

                        Files.list(path).forEach(p -> {
                            System.out.println(p);
                            response.append("<li>")
                                    .append("<a href=").append("\"/").append(rootPath.relativize(p))
                                    .append("\"").append(">").append(p.getFileName()).append("</a>")
                                    .append("</li>");
                        });

                        response.append("</ul>")
                                .append("</body>")
                                .append("</html>");
                        res.getHeaders().put("Content-Length", response.toString().length() + "");
                        outs.write(res.getRawResponse().getBytes());
                        outs.write(response.toString().getBytes());
                    } else {
                        outs.write(res.getRawResponse().getBytes());
                        outs.flush();
                        L.info("[path] " + path);
                        Files.copy(path, outs);
                    }
                    outs.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            });
            requestStream.run();
        }
    }
}
