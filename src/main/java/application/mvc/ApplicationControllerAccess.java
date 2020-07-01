package application.mvc;

import java.io.IOException;

public interface ApplicationControllerAccess {

    void importBuys() throws IOException;

    void togglBuy(Integer integer);

    void togglAll();

    void togglWin();

    void openBrowser();
}
