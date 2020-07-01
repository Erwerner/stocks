import application.initializer.ApplicationInitializer;
import ui.console.HybridViewFactory;

import java.io.IOException;

public class Starter {
    public static void main(String[] args) {
        new ApplicationInitializer(new HybridViewFactory(), new InfrastructureFactory());
    }
}
