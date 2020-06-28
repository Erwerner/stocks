package application.service;

import application.core.ApplicationData;

public class ApplicationService {
	private final ApplicationInput input;

	public ApplicationService(ApplicationInput input) {
		this.input = input;
	}

	public void execute(ApplicationData data) {
		data.setValue(input.readValue());
	}
}
