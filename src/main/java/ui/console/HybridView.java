package ui.console;

import application.core.exception.DateNotFound;
import application.core.exception.NoBuys;
import application.mvc.ApplicationControllerAccess;
import application.mvc.ApplicationViewAccess;
import helper.FilePersister;
import helper.IO;
import ui.template.Model;
import ui.template.View;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.time.LocalDate;
import java.util.HashMap;

import static ui.console.ConsoleControllerType.EXEC;

public class HybridView extends JFrame implements View {
    private final ApplicationViewAccess model;
    private HashMap<ConsoleControllerType, ConsoleController> controllers;
    private boolean active = true;


    public HybridView(Model model) {
        this.model = (ApplicationViewAccess) model;
        model.registerView(this);
        initWindow();
        initController(model);
        setVisible(true);
        run();
    }

    private void initWindow() {
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        setSize(1200, 700);
        setAlwaysOnTop(true);
    }

    @Override
    public void paint(Graphics arg0) {
        super.paint(arg0);
        System.out.println("paint");
        LocalDate lastDate = model.getLastDate();
        int col = 0;
        int maxValues = 300;
        int size = 1200 / maxValues;
        for (int i = maxValues; i >= 0; i--) {
            LocalDate date = lastDate.minusDays(i);
            Double[] line = model.getProfitLine(date);
            line[0] /= -15;
            line[1] /= -15;
            int zero = 200;
            line[0] += zero;
            line[1] += zero;
            arg0.drawLine(size * col, line[0].intValue(), size + size * col, line[1].intValue());
            arg0.drawLine(size * col, zero, size + size * col, zero);
            col += 1;
        }
        Double old = model.getTotalLine(lastDate.minusDays(1))[0];
        Double neu = model.getTotalLine(lastDate)[0];
        System.out.println("old:  " + old);
        System.out.println("new:  " + neu);
        System.out.println("diff: " + (neu - old));
        System.out.println("win:  " + (neu - model.getCostsAtDate(lastDate)));
        System.out.println((100 * neu / model.getCostsAtDate(lastDate)-100 ) + " %");
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
