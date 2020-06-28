package ui.console;

import application.mvc.ApplicationControllerAccess;

public class ConsoleControllerFactory {
    public ConsoleController initDoController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                model.execute();
            }
        };
    }
}
