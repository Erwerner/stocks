package ui.console;

import application.mvc.ApplicationControllerAccess;
import helper.ResourceFileReader;
import helper.ResourceNotFound;
import ui.template.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import static ui.console.ConsoleControllerType.*;

public class ConsoleControllerFactory {

    public HashMap<ConsoleControllerType, ConsoleController> initController(HybridView hybridView, Model model) {
        HashMap<ConsoleControllerType, ConsoleController> controllers = new HashMap<>();
        controllers.put(ConsoleControllerType.EXIT, hybridView.initExitController((ApplicationControllerAccess) model));
        controllers.put(ConsoleControllerType.BUYS, hybridView.initBuysController((ApplicationControllerAccess) model));
        controllers.put(ConsoleControllerType.LINE, hybridView.initLinesController((ApplicationControllerAccess) model));
        controllers.put(EXEC,
                initDoController((ApplicationControllerAccess) model));
        controllers.put(TOGL,
                initTogglController((ApplicationControllerAccess) model));
        controllers.put(RNGE,
                hybridView.initRangeController((ApplicationControllerAccess) model));
        controllers.put(TGAL,
                initTogglAllController((ApplicationControllerAccess) model));
        controllers.put(TGWN,
                initTogglWinController((ApplicationControllerAccess) model));
        return controllers;
    }

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
