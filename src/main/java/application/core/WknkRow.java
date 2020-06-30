package application.core;

import application.core.exception.DateNotFound;

import java.time.LocalDate;
import java.util.List;

public class WknkRow {
    private final List<WknPoint> wknPoints;

    public WknkRow(List<WknPoint> wknPoints) {
        this.wknPoints = wknPoints;
    }

    public List<WknPoint> getWknPoints() {
        return wknPoints;
    }

    public WknPoint getPointAtDate(LocalDate date) throws DateNotFound {
        for(WknPoint wknPoint : wknPoints){
            if(wknPoint.getDate().equals(date))
                return wknPoint;
        }
        throw new DateNotFound(date);
    }
}
