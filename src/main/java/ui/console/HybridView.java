package ui.console;

import application.core.exception.DateNotFound;
import application.core.exception.NoBuys;
import application.mvc.ApplicationControllerAccess;
import application.mvc.ApplicationViewAccess;
import helper.IO;
import ui.template.Model;
import ui.template.View;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;

public class HybridView extends JFrame implements View {
    private final ApplicationViewAccess model;
    private HashMap<ConsoleControllerType, ConsoleController> controllers;
    private boolean active = true;

    private Label textfield;

    public HybridView(Model model) {
        this.model = (ApplicationViewAccess) model;
        model.registerView(this);
        initWindow();
        initController(model);
        run();
    }

    private void initWindow() {
        setSize(800, 800);
        textfield = new Label();
        setAlwaysOnTop(true);
        add(textfield);
    }

    @Override
    public void paint(Graphics arg0) {
        String text = "";
        LocalDate last = model.getLastDate();
        try {
            LocalDate date = model.getFirstDate();
            while (!date.isAfter(last)) {
                Double[] line = model.getLine(date);
                text += "\n > " + line[0] + ";" + line[1];
                date = date.plusDays(1);
            }
        } catch (NoBuys noBuys) {
            noBuys.printStackTrace();
        }
        System.out.println(text);
        textfield.setText(text);
        super.paint(arg0);
    }

    private void run() {
        this.show();
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
        controllers.put(ConsoleControllerType.EXEC,
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
