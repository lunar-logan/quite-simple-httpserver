package org.qshs.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Created by Dell on 22-08-2016.
 */
public class LogUtil {

    public static Logger getLogger(String className) {
        Logger L = Logger.getLogger(className);
        L.setUseParentHandlers(false);
        L.addHandler(new ConsoleHandler() {

            @Override
            public void publish(LogRecord record) {
                System.err.println(String.format(
                        "%s/%s#%s: %s",
                        record.getLevel().getName().charAt(0),
                        record.getSourceClassName(),
                        record.getSourceMethodName(),
                        record.getMessage()
                ));
            }
        });
        return L;
    }


}
