import application.service.ApplicationInput;
import application.initializer.InputFactory;
import infrastructure.input.InfrastructureInput;

public class InfrastructureFactory extends InputFactory {
    @Override
    public ApplicationInput getInput() {
        return new InfrastructureInput();
    }
}
