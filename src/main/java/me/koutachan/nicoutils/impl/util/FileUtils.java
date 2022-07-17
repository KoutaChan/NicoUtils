package me.koutachan.nicoutils.impl.util;

import java.io.*;
import java.net.URL;

public class FileUtils {

    public static void downloadFileFromURL(String url, File output) {
        if (url == null || output == null || (output.getParentFile() != null && !output.getParentFile().exists())) {
            throw new IllegalStateException();
        }

        InputStream stream = null;
        OutputStream out = null;

        try {
            stream = new URL(url).openStream();
            out = new BufferedOutputStream(new FileOutputStream(output));

            for (int i; (i = stream.read()) != -1;) {
                out.write(i);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            close(stream);
            close(out);
        }
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
