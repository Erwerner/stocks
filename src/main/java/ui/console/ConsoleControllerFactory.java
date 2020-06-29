package ui.console;

import application.mvc.ApplicationControllerAccess;

import java.io.IOException;
import java.time.LocalDate;

public class ConsoleControllerFactory {
    public ConsoleController initDoController(ApplicationControllerAccess model) {
        return new ConsoleController(model) {
            @Override
            public void execute() {
                try {
                    model.addWkn("xy");
                    model.addWkn("abc");
                    model.addBuy("xy", LocalDate.parse("2020-06-18"), 10);
                    model.addBuy("xy", LocalDate.parse("2020-06-24"), 10);
                    model.addBuy("abc", LocalDate.parse("2020-06-25"), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
