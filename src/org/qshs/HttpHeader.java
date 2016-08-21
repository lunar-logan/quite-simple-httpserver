package org.qshs;

import org.qshs.util.LogUtil;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * This class represents the HTTP Headers
 */
public class HttpHeader {
    private static final Logger L = LogUtil.getLogger(HttpHeader.class.getName());

    private final HashMap<String, String> headers;

    public HttpHeader(String data) {
        headers = new HashMap<>();
        parse(data);
    }

    public HttpHeader() {
        headers = new HashMap<>();
    }

    public void parse(String rawHeaders) {

        // As per HTTP spec each header line is separated by CRLF (\r\n)
        String[] tokens = rawHeaders.split("\r\n");
        for (String line : tokens) {
            // headers are of the following format:
            // Header-Name: some value1; some value 2
            int colonIndex = line.indexOf(":");
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex);
                String val = line.substring(colonIndex + 1);
                headers.put(fixHeaderName(key), fixHeaderValue(val));
            } else {
                L.warning("Invalid header \"" + line + "\"");
            }
        }
    }

    private String fixHeaderValue(String val) {
        return val.trim();
    }

    private String fixHeaderName(String name) {
        char[] ncs = name.trim().toLowerCase().toCharArray();
        boolean uppercaseFlag = true;
        for (int i = 0; i < ncs.length; i++) {
            if (uppercaseFlag) {
                ncs[i] = Character.toUpperCase(ncs[i]);
                uppercaseFlag = false;
            }

            if (ncs[i] == '-') {
                uppercaseFlag = true;
            }
        }
        return new String(ncs);
    }

    public void put(String key, String val) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(val);
        headers.put(fixHeaderName(key), fixHeaderValue(val));
    }

    public String get(String key) {
        return headers.get(key);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        headers.forEach((k, v) -> res.append(k).append(": ").append(v).append("\r\n"));
        return res.toString();
    }
}
