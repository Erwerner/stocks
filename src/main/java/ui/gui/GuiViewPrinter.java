package ui.gui;

import application.core.model.Value;
import application.mvc.ApplicationViewAccess;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuiViewPrinter {

    public void drawAbsolute(Graphics arg0, ApplicationViewAccess model, Integer maxRange, int width) {
        List<Value[]> lines = model.getLines(maxRange);
        List<Double[]> absoluteLines = lines.stream().map(line -> new Double[]{line[0].getValue(), line[1].getValue()}).collect(Collectors.toList());
        drawLine(arg0, absoluteLines, 300, width / maxRange, -0.045);
    }

    public void drawRois(Graphics arg0, ApplicationViewAccess model) {
        List<Double[]> lines;
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
        int zero = 300;
        Double scale = -400.0;
        drawMarkLine(arg0, 0.1 * scale + zero, lines.size());
        drawMarkLine(arg0, -0.1 * scale + zero, lines.size());
        drawMarkLine(arg0, 0.2 * scale + zero, lines.size());
        drawLine(arg0, lines, zero, 1, scale);
    }

    private void drawMarkLine(Graphics arg0, Double plusTen, int size) {
        for (int col = 0; col < size; col++) {
            arg0.drawLine(col, plusTen.intValue(), 1 + col, plusTen.intValue());
        }
    }

    private void drawLine(Graphics arg0, List<Double[]> absoluteLines, int zero, int size, Double scale) {
        for (Double[] printLine1 : absoluteLines) {
            printLine1[0] *= scale;
            printLine1[1] *= scale;
            printLine1[0] += zero;
            printLine1[1] += zero;
        }
        int col = 0;
        for (Double[] printLine : absoluteLines) {
            int from = size * col;
            arg0.drawLine(from, printLine[0].intValue(), size + from, printLine[1].intValue());
            arg0.drawLine(from, zero, size + from, zero);
            col += 1;
        }
    }
}
