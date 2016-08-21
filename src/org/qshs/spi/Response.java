package org.qshs.spi;

import org.qshs.HttpHeader;

import java.nio.file.Path;

/**
 * @author Anurag Gautam
 */
public interface Response {
    String getRawResponse();

    Path getFilePath();

    HttpHeader getHeaders();
}
