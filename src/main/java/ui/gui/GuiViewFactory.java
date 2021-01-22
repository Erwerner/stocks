package ui.gui;

import template.Model;
import template.ViewFactory;

public class GuiViewFactory extends ViewFactory {
    @Override
    public void makeViews(Model model) {
        new GuiView(model);
    }
}
