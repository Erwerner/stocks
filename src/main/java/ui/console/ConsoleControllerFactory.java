package ui.console;

import application.mvc.ApplicationControllerAccess;
import ui.template.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import static ui.console.ConsoleControllerType.*;

public class ConsoleControllerFactory {

    public HashMap<ConsoleControllerType, ConsoleController> initController(HybridView hybridView, Model model) {
        HashMap<ConsoleControllerType, ConsoleController> controllers = new HashMap<>();
        controllers.put(EXIT, hybridView.initExitController((ApplicationControllerAccess) model));
        controllers.put(BUYS, hybridView.initBuysController((ApplicationControllerAccess) model));
        controllers.put(LINE, hybridView.initLinesController((ApplicationControllerAccess) model));
        controllers.put(RNGE, hybridView.initRangeController((ApplicationControllerAccess) model));
        controllers.put(EXEC, initDoController((ApplicationControllerAccess) model));
        controllers.put(TOGL, initTogglController((ApplicationControllerAccess) model));
        controllers.put(TGAL, initTogglAllController((ApplicationControllerAccess) model));
        controllers.put(TGWN, initTogglWinController((ApplicationControllerAccess) model));
        controllers.put(BRWS, initBrowserController((ApplicationControllerAccess) model));
        controllers.put(CASH, initCashController((ApplicationControllerAccess) model));
        return controllers;
    }

    private ConsoleController initCashController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                try {
                    String cash = new BufferedReader(new InputStreamReader(System.in)).readLine();
                    model.setCash(new Double(cash));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private ConsoleController initBrowserController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                model.openBrowser();
            }
        };
    }

    public ConsoleController initDoController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                try {
                    model.importBuys();
                } catch (IOException e) {
                    e.printStackTrace();
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
