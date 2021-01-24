package ui.console;

import application.core.model.AssetBuy;
import application.mvc.ApplicationController;
import application.mvc.ApplicationControllerAccess;
import application.mvc.ApplicationViewAccess;
import helper.IO;
import template.Model;
import template.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static ui.console.ConsoleControllerType.*;

public class ConsoleView implements View {
    private final ApplicationViewAccess model;
    private final HashMap<ConsoleControllerType, ApplicationController> controllers = new HashMap<>();
    private boolean active = true;
    public static Integer maxRange = 1000;
    public static boolean showBuyLines = false;
    public static boolean showRois = true;
    private final ConsoleViewPrinter consoleViewPrinter;


    public ConsoleView(Model model) {
        this.model = (ApplicationViewAccess) model;
        model.registerView(this);
        initController();
        consoleViewPrinter = new ConsoleViewPrinter();
        run();
    }

    private void initController() {
        ApplicationControllerAccess applicationControllerAccess = (ApplicationControllerAccess) this.model;
        Consumer<ApplicationControllerAccess> commandExit = (access) -> {
            System.out.println("Closing View...");
            active = false;
            applicationControllerAccess.refreshViews();
        };
        Consumer<ApplicationControllerAccess> commandBuys = (access) -> {
            showBuyLines = !showBuyLines;
            applicationControllerAccess.refreshViews();
        };
        Consumer<ApplicationControllerAccess> commandRange = (access) -> {
            try {
                maxRange = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
                applicationControllerAccess.refreshViews();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Consumer<ApplicationControllerAccess> commandRois = (access) -> {
            showRois = !showRois;
            applicationControllerAccess.refreshViews();
        };

        addController(EXIT, commandExit);
        addController(BUYS, commandBuys);
        addController(RNGE, commandRange);
        addController(ROIS, commandRois);
        addInputController(TOGL, (access, input) -> Arrays.stream(input.split(",")).map(Integer::parseInt).forEach(access::togglBuy));
        addInputController(CDAT, (access, input) -> access.changeDate(LocalDate.parse(input)));
        addController(REFR, ApplicationControllerAccess::refreshViews);
        addController(TGWN, ApplicationControllerAccess::togglWin);
        addController(TGAL, ApplicationControllerAccess::togglAll);
        addController(BRWS, ApplicationControllerAccess::openBrowser);
        addController(BRWT, ApplicationControllerAccess::browseWatch);
        addController(SOLD, ApplicationControllerAccess::togglSold);
        addController(GRUP, ApplicationControllerAccess::group);
    }

    private void addInputController(ConsoleControllerType togl, BiConsumer<ApplicationControllerAccess, String> command) {
        controllers.put(togl, new ConsoleInputController((ApplicationControllerAccess) model, command));
    }

    private void addController(ConsoleControllerType exit, Consumer<ApplicationControllerAccess> command) {
        controllers.put(exit, new ApplicationController((ApplicationControllerAccess) model, command));
    }

    public void print() {
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

    @Override
    public void update() {
        this.print();
    }
}
