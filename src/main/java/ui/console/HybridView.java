package ui.console;

import application.mvc.ApplicationControllerAccess;
import application.mvc.ApplicationViewAccess;
import helper.IO;
import ui.template.Model;
import ui.template.View;

import javax.swing.*;
import java.awt.*;
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
        setSize(200, 250);
        textfield = new Label();
        setAlwaysOnTop(true);
        add(textfield);
    }

    @Override
    public void paint(Graphics arg0) {
        textfield.setText(model.getValue());
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
