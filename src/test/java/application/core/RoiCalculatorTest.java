package application.core;

import org.junit.Before;
import org.junit.Test;
import utils.UnitTest;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class RoiCalculatorTest extends UnitTest {
    RoiCalculator cut;

    @Before
    void setup() {
        cut = new RoiCalculator();
    }

    @Test
    public void when_roi_is_calculated_for_half_a_year_then_roi_is_double_of_percentage() {
        LocalDate startDate = LocalDate.of(2001, 01, 01);
        LocalDate endDate = startDate.plusDays(356 / 2);
        double percentageDifference = 0.5;
        double act = RoiCalculator.calcRoiFromRange(startDate, endDate, percentageDifference);
        assertEquals(percentageDifference * 2, act);
    }
}
