package application.mvc;

import ui.template.Model;
import application.core.ApplicationData;
import application.service.ApplicationInput;
import application.service.ApplicationService;

public class ApplicationModel extends Model implements
		ApplicationControllerAccess, ApplicationViewAccess {
	private final ApplicationData data;
	private final ApplicationService service;

	public ApplicationModel(ApplicationInput input) {
		data = new ApplicationData();
		service = new ApplicationService(input);
	}

	// View
	@Override
	public String getValue() {
		return data.getValue();
	}

	// Controller
	@Override
	public void execute() {
		service.execute(data);
		notifyViews();
	}

}
