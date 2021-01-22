package ui.console;

import template.Model;
import template.ViewFactory;

public class ConsoleViewFactory extends ViewFactory {
    @Override
    public void makeViews(Model model) {
        new ConsoleView(model);
    }
}
