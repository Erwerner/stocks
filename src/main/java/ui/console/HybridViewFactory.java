package ui.console;

import ui.template.Model;
import ui.template.ViewFactory;

public class HybridViewFactory extends ViewFactory {
    @Override
    public void makeViews(Model model) {
        new HybridView(model);
    }
}
