package ui.console;

import application.mvc.ApplicationController;
import application.mvc.ApplicationControllerAccess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ConsoleInputController extends ApplicationController {
    final BiConsumer<ApplicationControllerAccess, String> inputCommand;

    public ConsoleInputController(ApplicationControllerAccess applicationControllerAccess, BiConsumer<ApplicationControllerAccess, String> inputCommand) {
        super(applicationControllerAccess, null);
        this.inputCommand = inputCommand;
    }

    @Override
    public void execute() {
        try {
            inputCommand.accept(model, new BufferedReader(new InputStreamReader(System.in)).readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
