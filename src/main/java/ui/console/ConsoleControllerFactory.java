package ui.console;

import application.mvc.ApplicationControllerAccess;
import helper.ResourceFileReader;
import helper.ResourceNotFound;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

    public ConsoleController initTogglAllController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {

            @Override
            public void execute() {
                        model.togglAll();
            }
        };
    }

    public ConsoleController initTogglController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                try {
                    String ids = new BufferedReader(new InputStreamReader(System.in))
                            .readLine();
                    String[] buyIds = ids.split(",");
                    for (String buyId : buyIds) {
                        model.togglBuy(Integer.parseInt(buyId));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public ConsoleController initTogglWinController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                model.togglWin();
            }
        };
    }
}
