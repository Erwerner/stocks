package helper;

import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ResourceFileReader {
    private static final ResourceFileReader resourceFileReader = new ResourceFileReader();

    public static String readResource(String fileName) throws IOException {
        ClassLoader classLoader = resourceFileReader.getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            if (inputStream == null)
                throw new IOException();
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            System.out.println("Could not readResource file: " + fileName);
            throw e;
        }
    }

    public static String[] getFilenamesInResourceFolder(String foldername) throws ResourceNotFound {
        String directory = foldername.charAt(0) != '/' ? "/" + foldername : foldername;
        URL resource = String.class.getResource(directory);
        if (resource == null)
            throw new ResourceNotFound(directory);
        String path = resource.getPath();
        return new File(path).list();
    }

    public static void open(String fileName) {
        try {
            if (!Desktop.isDesktopSupported()) {
                throw new RuntimeException("Desktop not supported");
            }
            URL dir_url = ClassLoader.getSystemResource(fileName);
            File file = new File(dir_url.toURI());
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
