import application.initializer.ApplicationInitializer;
import template.ViewFactory;
import ui.console.ConsoleViewFactory;
import ui.gui.GuiViewFactory;

import java.util.Arrays;
import java.util.List;

public class Starter {
    public static void main(String[] args) {
        List<ViewFactory> viewFactorys = Arrays.asList(new GuiViewFactory(), new ConsoleViewFactory());
        new ApplicationInitializer(viewFactorys, new InfrastructureFactory());
    }
}
