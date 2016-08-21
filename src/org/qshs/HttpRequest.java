package org.qshs;

import org.qshs.spi.Request;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dell on 22-08-2016.
 */
public class HttpRequest implements Request {
    private static final String SP = " ";
    private static final String CR = "\r";
    private static final String LF = "\n";
    private static final String CRLF = CR + LF;
    private static final Set<String> SUPPORTED_VERBS = new HashSet<>(Arrays.asList("GET", "HEAD"));

    private final String raw;

    private String method;
    private String location;
    private String version;
    private boolean badRequest;
    private HttpHeader headers;

    public HttpRequest(String raw) {
        this.raw = raw;
        this.parse();
    }

    private void parse() {
        String[] tokens = raw.split(CRLF);

        // Just parse the request line to get the file location
        // We can safely ignore the rest headers
        if (tokens.length > 0) {
            parseRequestLine(tokens[0]);
        }
    }

    private void parseRequestLine(String reqLine) {
        String rl = reqLine.replaceAll("[ \t]+", " ").trim();
        String[] mlv = rl.split(" ");
        if (mlv.length == 3) {
            this.method = mlv[0];
            this.location = mlv[1];
            this.version = mlv[2];
            this.badRequest = !validateRequestLine();
        }
    }


    private boolean validateRequestLine() {
        return (
                (method != null) && SUPPORTED_VERBS.contains(method) &&
                        (location != null) &&
                        (version != null && version.matches("^HTTP/1\\.[0-9]$"))
        );
    }

    @Override
    public String toString() {
        if (!badRequest)
            return method + SP + location + SP + version;
        return "400 Bad Request";
    }

    @Override
    public String getRawRequest() {
        return raw;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public HttpHeader getHeaders() {
        return headers;
    }

    @Override
    public boolean isBad() {
        return badRequest;
    }
}
