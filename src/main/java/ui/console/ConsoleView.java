package ui.console;

import application.core.model.AssetBuy;
import application.mvc.ApplicationControllerAccess;
import application.mvc.ApplicationViewAccess;
import helper.IO;
import template.Model;
import template.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ConsoleView implements View {
    private final ApplicationViewAccess model;
    private final HashMap<ConsoleControllerType, ConsoleController> controllers;
    private boolean active = true;
    public static Integer maxRange = 1000;
    public static boolean showBuyLines = false;
    public static boolean showRois = true;
    private final ConsoleViewPrinter consoleViewPrinter;


    public ConsoleView(Model model) {
        this.model = (ApplicationViewAccess) model;
        model.registerView(this);
        controllers = new ConsoleControllerFactory().initController(this, model);
        consoleViewPrinter = new ConsoleViewPrinter();
        run();
    }

    public void print( ) {
        consoleViewPrinter.printWatchAll(model);
        consoleViewPrinter.printAssetSize(model);
        if (!AssetBuy.showSold) {
            consoleViewPrinter.printWknTypeSum(model);
        }
        consoleViewPrinter.printChangeDate(model);
        consoleViewPrinter.printBuys(model);
        consoleViewPrinter.printToday(model);
        consoleViewPrinter.printBuyWatch(model);
        if (!AssetBuy.showSold) {
            consoleViewPrinter.printBuyCash(model);
        }
        consoleViewPrinter.printGroups(model);
        consoleViewPrinter.printConfig();

    }

    private void run() {
        try {
            ((ApplicationControllerAccess) model).importWkns();
            ((ApplicationControllerAccess) model).importBuys();
            ((ApplicationControllerAccess) model).importCash();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (active) {
            ConsoleControllerType controller = (ConsoleControllerType) IO
                    .getEnumFromInput("Choose Command",
                            ConsoleControllerType.values());
            controllers.get(controller).execute();
        }
    }


    public ConsoleController initLinesController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                model.refreshViews();
            }
        };
    }

    public ConsoleController initBuysController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                showBuyLines = !showBuyLines;
                model.refreshViews();
            }
        };
    }


    public ConsoleController initRangeController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                try {
                    maxRange = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
                    model.refreshViews();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public ConsoleController initExitController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                System.out.println("Closing View...");
                model.refreshViews();
                active = false;
            }
        };
    }

    @Override
    public void update() {
        this.print();
    }

    public ConsoleController initRefreshController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                model.refreshViews();
            }
        };
    }

    public ConsoleController initRoisController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                showRois = !showRois;
                model.refreshViews();
            }
        };
    }
}
