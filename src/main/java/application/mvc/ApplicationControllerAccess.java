package application.mvc;

import java.io.IOException;
import java.time.LocalDate;

public interface ApplicationControllerAccess {

    void importBuys() throws IOException;

    void togglBuy(Integer integer);

    void togglAll();

    void togglWin();

    void openBrowser();

    void setCash(double cash);

    void importCash() throws IOException;

    void browseWatch();

    void addCash(Double aDouble);

    void changeDate(LocalDate date);

    void togglSold();

    void importWkns();
}
