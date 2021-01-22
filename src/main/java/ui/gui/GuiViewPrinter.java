package ui.gui;

import application.core.model.Value;
import application.mvc.ApplicationViewAccess;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiViewPrinter {

    public void drawLines(Graphics arg0, ApplicationViewAccess model, Integer maxRange, int width) {
        int col = 0;
        int size;
        int zero;
        Double scale;
        size = width / maxRange;
        List<Double[]> lines;
        Double minusTen = 0.0;
        Double plusTen = 0.0;
        Double plusTwenty = 0.0;
        List<Value[]> relativeLines = model.getRelativeLines(maxRange);
        lines = new ArrayList<>();
        for (Value[] relativeLine : relativeLines) {
            Double[] line;
            //if (showLines == 1) {
                //line = new Double[]{relativeLine[0].getPercentage(), relativeLine[1].getPercentage()};
            //} else {
                line = new Double[]{relativeLine[0].getValue(), relativeLine[1].getValue()};
            //}
            lines.add(line);
        }
        //if (showLines == 1) {
        //    zero = 300;
        //    scale = -800.0;
        //} else {
            zero = 300;
            scale = -0.045;
        //}
        for (Double[] printLine : lines) {
            plusTen = 0.1;
            minusTen = -0.1;
            plusTwenty = 0.2;
            printLine[0] *= scale;
            printLine[1] *= scale;
            printLine[0] += zero;
            printLine[1] += zero;
            minusTen *= scale;
            minusTen += zero;
            plusTen *= scale;
            plusTen += zero;
            plusTwenty *= scale;
            plusTwenty += zero;
        }
        for (Double[] printLine : lines) {
            arg0.drawLine(size * col, printLine[0].intValue(), size + size * col, printLine[1].intValue());
            arg0.drawLine(size * col, zero, size + size * col, zero);
            arg0.drawLine(size * col, plusTen.intValue(), size + size * col, plusTen.intValue());
            arg0.drawLine(size * col, minusTen.intValue(), size + size * col, minusTen.intValue());
            arg0.drawLine(size * col, plusTwenty.intValue(), size + size * col, plusTwenty.intValue());
            col += 1;
        }
    }


    public void drawRois(Graphics arg0, ApplicationViewAccess model) {
        int col = 0;
        int zero;
        Double scale;
        List<Double[]> lines;
        Double minusTen = 0.0;
        Double plusTen = 0.0;
        Double plusTwenty = 0.0;
        List<Double> rois = model.getRoisWithSold();
        lines = new ArrayList<>();
        Double lastRoi = 0.0;
        for (Double roi : rois) {
            if (!roi.isNaN()) {
                Double[] line;
                line = new Double[]{lastRoi, roi};
                lines.add(line);
                lastRoi = roi;
            }
        }
        zero = 300;
        scale = -400.0;
        for (Double[] printLine : lines) {
            plusTen = 0.1;
            plusTwenty = 0.2;
            minusTen = -0.1;
            printLine[0] *= scale;
            printLine[1] *= scale;
            printLine[0] += zero;
            printLine[1] += zero;
            minusTen *= scale;
            minusTen += zero;
            plusTen *= scale;
            plusTen += zero;
            plusTwenty *= scale;
            plusTwenty += zero;
        }
        for (Double[] printLine : lines) {
            arg0.drawLine(col, printLine[0].intValue(), 1 + col, printLine[1].intValue());
            arg0.drawLine(col, zero, 1 + col, zero);
            arg0.drawLine(col, plusTen.intValue(), 1 + col, plusTen.intValue());
            arg0.drawLine(col, minusTen.intValue(), 1 + col, minusTen.intValue());
            arg0.drawLine(col, plusTwenty.intValue(), 1 + col, plusTwenty.intValue());
            col += 1;
        }
    }

}
