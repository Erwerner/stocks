package ui.console;

import application.core.StockBuy;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static ui.console.ConsoleControllerType.*;

public class HybridView extends JFrame implements View {
    private final ApplicationViewAccess model;
    private HashMap<ConsoleControllerType, ConsoleController> controllers;
    private boolean active = true;
    private Integer maxRange = 1000;
    private int width = 1200;


    public HybridView(Model model) {
        this.model = (ApplicationViewAccess) model;
        model.registerView(this);
        initWindow();
        initController(model);
        run();
    }

    private void initWindow() {
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        setSize(width, 650);
        setAlwaysOnTop(true);
    }

    @Override
    public void paint(Graphics arg0) {
        super.paint(arg0);
        drawLines(arg0);
        printBuyLines(arg0);
        printWkns();
        printToday();
    }

    private void printBuyLines(Graphics arg0) {
        List<Boolean> buys = model.getBuyLines(maxRange);
        int col = 0;
        int size = width / this.maxRange;
        for (Boolean buy : buys) {
            if (buy)
                arg0.drawLine(size * col, 0, size * col, 900);
            col += 1;
        }
    }

    private void drawLines(Graphics arg0) {
        List<Double[]> printLines = model.getRelativeLines(maxRange);
        int col = 0;
        int size = width / this.maxRange;
        int zero = 300;
        int scale = -800;
        for (Double[] printLine : printLines) {
            printLine[0] *= scale;
            printLine[1] *= scale;
            printLine[0] += zero;
            printLine[1] += zero;
            arg0.drawLine(size * col, printLine[0].intValue(), size + size * col, printLine[1].intValue());
            arg0.drawLine(size * col, zero, size + size * col, zero);
            col += 1;
        }
    }

    private void printToday() {
        LocalDate lastDate = model.getLastDate();
        Double old = model.getTotalLine(lastDate.minusDays(1))[0];
        Double neu = model.getTotalLine(lastDate)[0];
        System.out.println("old:  " + old);
        System.out.println("new:  " + neu);
        System.out.println("diff: " + (neu - old));
        System.out.println("win:  " + (neu - model.getCostsAtDate(lastDate)));
        System.out.println((100 * neu / model.getCostsAtDate(lastDate) - 100) + " %");
    }

    private void printWkns() {
        int count = 0;
        for (StockBuy buy : model.getAllBuys()) {
            String active = " ";
            if (buy.isActive())
                active = "X";
            Double buyWin = ((Double) (model.getBuyWin(buy) * 10000)).intValue() / 100.0;
            System.out.println(active + " [" + count + "] " + buy.getWkn() + " " + buy.getDate() + " (" + buyWin + "%) " + model.getWknName(buy.getWkn()));
            count++;
        }
    }


    private void run() {
        this.show();
        controllers.get(EXEC).execute();
        while (active) {
            ConsoleControllerType controller = (ConsoleControllerType) IO
                    .getEnumFromInput("Choose Command",
                            ConsoleControllerType.values());
            controllers.get(controller).execute();
        }
    }

    private void initController(Model model) {
        controllers = new HashMap<>();
        ConsoleControllerFactory controllerFactory = new ConsoleControllerFactory();
        controllers.put(ConsoleControllerType.EXIT, initExitController(model));
        controllers.put(EXEC,
                controllerFactory.initDoController((ApplicationControllerAccess) model));
        controllers.put(TOGL,
                controllerFactory.initTogglController((ApplicationControllerAccess) model));
        controllers.put(RNGE,
                initRangeController((ApplicationControllerAccess) model));
        controllers.put(TGAL,
                controllerFactory.initTogglAllController((ApplicationControllerAccess) model));
        controllers.put(TGWN,
                controllerFactory.initTogglWinController((ApplicationControllerAccess) model));
    }


    private ConsoleController initRangeController(ApplicationControllerAccess model) {
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

    private ConsoleController initExitController(Model model) {
        return new ConsoleController((ApplicationControllerAccess) model) {
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
}
