package helper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FilePersister {

    public void persistString(String folderPath, String fileName, String output) {
        try {
            File file = getFile(folderPath, fileName);
            FileUtils.writeStringToFile(file, output, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean pathExists(String folderPath, String fileName) {
        return getFile(folderPath, fileName).exists();
    }

    public String getPersistedString(String folderPath, String fileName) {
        try {
            File file = getFile(folderPath, fileName);
            return FileUtils.readFileToString(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(String folderPath, String fileName) {
        try {
            File file = getFile(folderPath, fileName);
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getFile(String folderPath, String fileName) {
        String path;
        if (fileName.equals(""))
            path = folderPath;
        else
            path = folderPath + "/" + fileName;
        return new File(path);
    }
}
