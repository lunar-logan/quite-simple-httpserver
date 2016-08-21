package org.qshs.tests;

import junit.framework.TestCase;
import org.qshs.HttpHeader;

/**
 * Created by Dell on 22-08-2016.
 */
public class HttpHeaderTest extends TestCase {
    public void testGet() throws Exception {
        HttpHeader header = new HttpHeader("a:65\r\nbb: 66\r\nc-d : abc  \r\nX-:yy  ");
        System.out.println(header);
        assertEquals(header.get("Bb"), "66");
        assertEquals(header.get("A"), "65");
        assertEquals(header.get("C-D"), "abc");
        assertEquals(header.get("X-"), "yy");
    }

}