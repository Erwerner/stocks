package application.initializer;

import application.mvc.ApplicationModel;
import template.ViewFactory;

import java.util.List;

public class ApplicationInitializer {
    public ApplicationInitializer(List<ViewFactory> viewFactories, InputFactory inputFactory) {
        ApplicationModel model = new ApplicationModel(inputFactory.getInput());
        viewFactories.forEach(factory -> factory.makeViews(model));
    }
}
