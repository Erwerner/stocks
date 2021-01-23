package application.mvc;

import java.util.function.Consumer;

public class ApplicationController {

    protected final ApplicationControllerAccess model;
    final Consumer<ApplicationControllerAccess> command;

    public ApplicationController(ApplicationControllerAccess model, Consumer<ApplicationControllerAccess> command) {
        this.model = model;
        this.command = command;
    }

    public void execute() {
        command.accept(model);
    }
}
