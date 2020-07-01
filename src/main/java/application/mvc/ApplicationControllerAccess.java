package application.mvc;

import java.io.IOException;

public interface ApplicationControllerAccess {
    void addWkn(String wkn) throws IOException;

    void importBuys() throws IOException;

    void export();

    void togglBuy(Integer integer);

    void togglAll();

    void togglWin();

    void openBrowser();
}
