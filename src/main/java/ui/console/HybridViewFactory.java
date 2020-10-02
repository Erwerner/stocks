package ui.console;

import template.Model;
import template.ViewFactory;

public class HybridViewFactory extends ViewFactory {
    @Override
    public void makeViews(Model model) {
        new HybridView(model);
    }
}
