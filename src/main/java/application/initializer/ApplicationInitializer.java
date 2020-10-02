package application.initializer;

import application.mvc.ApplicationModel;
import template.ViewFactory;

public class ApplicationInitializer {
    public ApplicationInitializer(ViewFactory viewFactory, InputFactory inputFactory) {
        ApplicationModel model = new ApplicationModel(inputFactory.getInput());
        viewFactory.makeViews(model);
    }
}
