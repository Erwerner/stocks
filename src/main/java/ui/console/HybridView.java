package ui.console;

import application.mvc.ApplicationControllerAccess;
import application.mvc.ApplicationViewAccess;
import helper.IO;
import ui.template.Model;
import ui.template.View;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class HybridView extends JFrame implements View {
    private final ApplicationViewAccess model;
    private final HashMap<ConsoleControllerType, ConsoleController> controllers;
    private boolean active = true;
    private Integer maxRange = 1000;
    private final int width = 1200;
    private boolean showBuyLines = false;
    private int showLines = 1;
    private final HybridViewPrinter hybridViewPrinter;


    public HybridView(Model model) {
        this.model = (ApplicationViewAccess) model;
        model.registerView(this);
        initWindow();
        controllers = new ConsoleControllerFactory().initController(this, model);
        hybridViewPrinter = new HybridViewPrinter();
        run();
    }

    private void initWindow() {
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        setSize(width, 550);
        setAlwaysOnTop(true);
    }

    @Override
    public void paint(Graphics arg0) {
        super.paint(arg0);
        hybridViewPrinter.drawLines(arg0, model, maxRange, width, showLines);
        if (showBuyLines)
            hybridViewPrinter.printBuyLines(arg0, model, maxRange, width);

        hybridViewPrinter.printUrls(model);
        hybridViewPrinter.printWatchAll(model);
        hybridViewPrinter.printAssetSize(model);
        hybridViewPrinter.printWknTypeSum(model);
        hybridViewPrinter.printChangeDate(arg0, model, maxRange, width);
        hybridViewPrinter.printBuys(model);
        hybridViewPrinter.printToday(model);
        hybridViewPrinter.printBuyWatch(model);
        hybridViewPrinter.printBuyCash(model);
    }

    private void run() {
        this.show();
        try {
            ((ApplicationControllerAccess) model).importBuys();
            ((ApplicationControllerAccess) model).importCash();
            //((ApplicationControllerAccess) model).togglBuy(7);
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
                showLines *= -1;
                repaint();
            }
        };
    }

    public ConsoleController initBuysController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                showBuyLines = !showBuyLines;
                repaint();
            }
        };
    }


    public ConsoleController initRangeController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                try {
                    maxRange = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
                    repaint();
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
                dispose();
                active = false;
            }
        };
    }

    @Override
    public void update() {
        this.repaint();
    }

    public ConsoleController initRefreshController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                repaint();
            }
        };
    }
}
