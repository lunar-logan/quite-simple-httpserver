package org.qshs.spi;

import org.qshs.HttpHeader;

/**
 * @author Anurag
 */
public interface Request {
    String getRawRequest();

    String getMethod();

    String getLocation();

    String getVersion();

    HttpHeader getHeaders();

    boolean isBad();
}
