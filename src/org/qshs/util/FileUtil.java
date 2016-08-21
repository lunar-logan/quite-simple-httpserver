package org.qshs.util;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Dell on 22-08-2016.
 */
public class FileUtil {
    private static final String HTTP_ROOT_NAME = "httpRoot";

    public static Path getPath(String path) {
        Path rootPath = Paths.get(System.getProperty("user.dir"), HTTP_ROOT_NAME);
        if (path.equals("/")) {
            path = "index.html";
        }
        Path filePath = rootPath.resolve(sanitizePath(path));
        return filePath;
    }


    private static String sanitizePath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path.replaceAll("\\.\\./", "");
    }
}
