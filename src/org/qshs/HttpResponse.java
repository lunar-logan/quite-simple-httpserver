package org.qshs;

import org.qshs.spi.Request;
import org.qshs.spi.Response;
import org.qshs.util.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Anurag Gautam
 */
public class HttpResponse implements Response {
    private static final String SP = " ";
    private static final String CR = "\r";
    private static final String LF = "\n";
    private static final String CRLF = CR + LF;

    private int responseCode;
    private String message;
    private long length = -1;
    private Path path;
    private HttpHeader header = new HttpHeader();

    public HttpResponse(Request req) {
        if (req.isBad()) {
            code(400);
        } else {
            String loc = req.getLocation();
            path = FileUtil.getPath(loc);
            if (Files.exists(path)) {
                code(200);
                try {
                    length = Files.size(path);
                } catch (IOException e) {
                    e.printStackTrace();
                    code(503);
                }
            } else {
                code(404);
            }
        }
    }

    private void code(int code) {
        this.responseCode = code;
        switch (responseCode) {
            case 200:
                message = "OK";
                break;
            case 400:
                message = "Bad Request";
                break;
            case 404:
                message = "Not Found";
                break;
            default:
                message = "Internal Server Error";
        }
    }


    @Override
    public String getRawResponse() {
        switch (responseCode) {
            case 200:
                if (path.toFile().isDirectory()) {
                    header.put("Content-type", "text/html");
                } else {
                    header.put("Content-Length", String.valueOf(length));
                    try {
                        header.put("Content-Type", Files.probeContentType(path));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 404:
                path = Paths.get(System.getProperty("user.dir"), "httpRoot", "404.html");
                try {
                    header.put("Content-Length", "" + Files.size(Paths.get(System.getProperty("user.dir"), "httpRoot", "404.html")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                header.put("Content-type", "text/html");
                break;
            case 400:
                path = Paths.get(System.getProperty("user.dir"), "httpRoot", "400.html");
                try {
                    header.put("Content-Length", "" + Files.size(Paths.get(System.getProperty("user.dir"), "httpRoot", "400.html")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                header.put("Content-type", "text/html");
                break;
            default:
                path = Paths.get(System.getProperty("user.dir"), "httpRoot", "503.html");
                try {
                    header.put("Content-Length", "" + Files.size(Paths.get(System.getProperty("user.dir"), "httpRoot", "503.html")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                header.put("Content-type", "text/html");
        }
        return "HTTP/1.1" + SP + responseCode + SP + message + CRLF +
                header.toString() +
                CRLF;
    }

    @Override
    public Path getFilePath() {
        return path;
    }

    @Override
    public HttpHeader getHeaders() {
        return header;
    }

}
