package application.mvc;

import java.io.IOException;
import java.time.LocalDate;

public interface ApplicationControllerAccess {

    void importBuys() throws IOException;

    void togglBuy(Integer integer);

    void togglAll();

    void togglWin();

    void openBrowser();

    void importCash() throws IOException;

    void browseWatch();

    void changeDate(LocalDate date);

    void togglSold();

    void importWkns();

    void group();

    void refreshViews();
}
