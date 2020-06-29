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
import java.time.LocalDate;
import java.util.HashMap;

import static ui.console.ConsoleControllerType.EXEC;

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
        setSize(800, 400);
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
                Double[] profitLine = model.getProfitLine(date);
                Double costsAtDate = model.getCostsAtDate(date);
                Double minusCosts = new Double(model.getCostsAtDate(date) * -1);

                text += "\n" + new Double(profitLine[0] / costsAtDate ).toString().replace(".", ",");
                text += ";" + new Double(profitLine[0] / model.getCostsAtDate(last) ).toString().replace(".", ",");
                text += ";0";
                text += ";"+ costsAtDate.toString().replace(".", ",") ;
                text += ";-"+minusCosts.toString().replace(".", ",");
                text += ";" + date.toString();

                date = date.plusDays(1);
            }
        } catch (NoBuys noBuys) {
        }
        new FilePersister().persistString("out","profit.csv",text);
        System.out.println(text);
        printTotalLine();
        printwkn(last);
        System.out.println(model.getCostsAtDate(last));
        textfield.setText(text);
        super.paint(arg0);
    }

    private void printTotalLine() {
        String text = "";
        LocalDate last = model.getLastDate();
        try {
            LocalDate date = model.getFirstDate();
            while (!date.isAfter(last)) {
                if (model.dateWasBuy(date))
                    text += "\n";
                text += "\n" + model.getTotalLine(date)[0].toString().replace(".", ",");
                date = date.plusDays(1);
            }
        } catch (NoBuys noBuys) {
        }
        System.out.println(text);
        new FilePersister().persistString("out","sum.csv",text);
    }

    private void printwkn(LocalDate last) {
        for (String wkn : model.getWkns()) {
            try {
                System.out.println(wkn + ": " + model.getValue(wkn, last).getValue());
            } catch (DateNotFound dateNotFound) {
                dateNotFound.printStackTrace();
            }
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
