package ui.console;

import application.mvc.ApplicationControllerAccess;

public abstract class ConsoleController {

    protected final ApplicationControllerAccess model;

    public ConsoleController(ApplicationControllerAccess model) {
        this.model = model;
    }

    public abstract void execute();
}
