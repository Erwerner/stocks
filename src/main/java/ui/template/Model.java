package ui.template;

import java.util.ArrayList;
import java.util.List;

public abstract class Model {
    private final List<View> views = new ArrayList<>();

    public void registerView(View view) {
        views.add(view);
    }

    public void notifyViews(){
        for(View view: views)
            view.update();
    }
}
