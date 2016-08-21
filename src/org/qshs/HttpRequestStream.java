package org.qshs;

import org.qshs.spi.Request;
import org.qshs.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Anurag Gautam
 */
public class HttpRequestStream implements Runnable {
    private final InputStream inputStream;
    private final BufferedInputStream bis;
    private final byte[] buf;

    private Function<Request, Void> callback;

    private static final Logger L = LogUtil.getLogger(HttpRequestStream.class.getName());

    public HttpRequestStream(InputStream inputStream) {
        this.inputStream = inputStream;
        this.bis = new BufferedInputStream(inputStream);
        this.buf = new byte[4096 * 2];
    }

    public void setOnRequest(Function<Request, Void> callback) {
        this.callback = callback;
    }

    private void read() {
        Thread streamThread = new Thread(() -> {
            while (true) {
                try {
                    int len = bis.read(buf);
                    if (len > 0 && len < buf.length) {
                        String raw = new String(buf, 0, len);
                        L.info("[raw request] " + raw);
                        HttpRequest request = new HttpRequest(raw);
                        callback.apply(request);
                    } else if (len > buf.length) {
                        L.log(Level.SEVERE, "[ request too big of size " + len + " bytes ]");
                    } else if (len == -1) {
                        inputStream.close();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        streamThread.setDaemon(true);
        streamThread.start();
    }

    @Override
    public void run() {
        read();
    }
}
