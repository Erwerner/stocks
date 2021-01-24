package application.service;

import application.core.model.ApplicationData;
import helper.ResourceFileReader;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

public class ExecuteService {
    public void browseWkns(ApplicationData data, HashSet<String> wkns) {

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            for (String wkn : wkns) {
                try {
                    Desktop.getDesktop().browse(new URI(data.getWknUrl(wkn)));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
                try {
                    File file = new File("/Users/eriwerne/Desktop/privat/git/stocks/src/main/resources/wkn/" + wkn);
                    Desktop desktop = Desktop.getDesktop();
                    if (file.exists())         //checks file exists or not
                        desktop.open(file);              //opens the specified file
                } catch (Exception e) {
                    ResourceFileReader.open("wkn/" + wkn);
                }
            }
        }
    }
}
