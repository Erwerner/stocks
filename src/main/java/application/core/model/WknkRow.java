package application.core.model;

import application.core.model.exception.DateNotFound;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class WknkRow {
    private final List<WknPoint> wknPoints;
    public WknPoint getPointAtDate(LocalDate date) throws DateNotFound {
        for(WknPoint wknPoint : wknPoints){
            if(wknPoint.getDate().equals(date))
                return wknPoint;
        }
        throw new DateNotFound(date);
    }
}
