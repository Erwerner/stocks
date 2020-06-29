package ui.console;

import application.mvc.ApplicationControllerAccess;
import helper.ResourceFileReader;
import helper.ResourceNotFound;

import java.io.IOException;

public class ConsoleControllerFactory {
    public ConsoleController initDoController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                try {
                    for (String wkn : ResourceFileReader.getFilenamesInResourceFolder("wkn")) {
                        model.addWkn(wkn);
                    }
                    model.importBuys();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ResourceNotFound resourceNotFound) {
                    resourceNotFound.printStackTrace();
                }
            }
        };
    }
}
