import application.initializer.ApplicationInitializer;
import infrastructure.input.InfrastructureInput;
import ui.console.HybridViewFactory;

import java.io.IOException;

public class Starter {
    public static void main(String[] args) throws IOException {
        new ApplicationInitializer(new HybridViewFactory(), new InfrastructureFactory());
    }
}
