package ui.gui;

import application.mvc.ApplicationViewAccess;
import template.Model;
import template.View;
import ui.console.ConsoleView;

import javax.swing.*;
import java.awt.*;

public class GuiView extends JFrame implements View {
    private final ApplicationViewAccess model;
    private final int width = 1500;
    private final GuiViewPrinter guiViewPrinter;


    public GuiView(Model model)  {
        this.model = (ApplicationViewAccess) model;
        model.registerView(this);
        initWindow();
        guiViewPrinter = new GuiViewPrinter();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        if (ConsoleView.showRois) {
            guiViewPrinter.drawRois(arg0, model);
        } else {
            guiViewPrinter.drawAbsolute(arg0, model, ConsoleView.maxRange, width);
        }

    }

    private void run() {
        this.show();
    }

    @Override
    public void update() {
        this.repaint();
    }
}
