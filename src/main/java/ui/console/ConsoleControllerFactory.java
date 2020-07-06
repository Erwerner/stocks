package ui.console;

import application.mvc.ApplicationControllerAccess;
import ui.template.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.HashMap;

import static ui.console.ConsoleControllerType.*;

public class ConsoleControllerFactory {

    public HashMap<ConsoleControllerType, ConsoleController> initController(HybridView hybridView, Model model) {
        HashMap<ConsoleControllerType, ConsoleController> controllers = new HashMap<>();
        controllers.put(EXIT, hybridView.initExitController((ApplicationControllerAccess) model));
        controllers.put(BUYS, hybridView.initBuysController((ApplicationControllerAccess) model));
        controllers.put(LINE, hybridView.initLinesController((ApplicationControllerAccess) model));
        controllers.put(RNGE, hybridView.initRangeController((ApplicationControllerAccess) model));
        controllers.put(REFR, hybridView.initRefreshController((ApplicationControllerAccess) model));
        controllers.put(EXEC, initDoController((ApplicationControllerAccess) model));
        controllers.put(TOGL, initTogglController((ApplicationControllerAccess) model));
        controllers.put(TGAL, initTogglAllController((ApplicationControllerAccess) model));
        controllers.put(TGWN, initTogglWinController((ApplicationControllerAccess) model));
        controllers.put(BRWS, initBrowserController((ApplicationControllerAccess) model));
        controllers.put(CASH, initCashController((ApplicationControllerAccess) model));
        controllers.put(BRWT, initBrowseWatchController((ApplicationControllerAccess) model));
        controllers.put(ADDC, initAddCashController((ApplicationControllerAccess) model));
        controllers.put(CDAT, initChangeDateController((ApplicationControllerAccess) model));
        return controllers;
    }

    private ConsoleController initChangeDateController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                try {
                    String input = null;
                    input = new BufferedReader(new InputStreamReader(System.in)).readLine();
                    LocalDate date = LocalDate.parse(input);
                    model.changeDate(date);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private ConsoleController initAddCashController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                try {
                    String cash = new BufferedReader(new InputStreamReader(System.in)).readLine();
                    model.addCash(new Double(cash));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private ConsoleController initBrowseWatchController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                model.browseWatch();
            }
        };
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

    private ConsoleController initDoController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                try {
                    model.importBuys();
                    model.importCash();
                    model.togglBuy(7);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private ConsoleController initTogglAllController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {

            @Override
            public void execute() {
                model.togglAll();
            }
        };
    }

    private ConsoleController initTogglController(ApplicationControllerAccess model) {
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

    private ConsoleController initTogglWinController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                model.togglWin();
            }
        };
    }
}
