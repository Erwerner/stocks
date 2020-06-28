package infrastructure.input;

import application.service.ApplicationInput;

public class InfrastructureInput extends ApplicationInput {
    @Override
    public String readValue() {
        return "Content from Infrastructure";
    }
}
