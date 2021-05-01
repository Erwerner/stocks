package infrastructure.input;

import application.core.model.AssetBuy;
import application.core.model.WknPoint;
import application.service.ApplicationInput;
import helper.ResourceFileReader;
import helper.ResourceNotFound;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InfrastructureInput extends ApplicationInput {
    @Override
    public ArrayList<WknPoint> getWknPoints(String wkn) throws IOException {
        ArrayList<WknPoint> wknPoints = new ArrayList<>();
        String[] lines = getWknFileLines(wkn);
        String wknName = lines[0];
        String wknType = lines[1];
        for (String line : lines) {
            if (line.equals(wknName))
                continue;
            if (line.equals(wknType))
                continue;
            String[] columns = line.split(" \t");
            String dateString = columns[0].substring(6, 10) + "-" + columns[0].substring(3, 5) + "-" + columns[0].substring(0, 2);
            wknPoints.add(new WknPoint(LocalDate.parse(dateString), Double.valueOf(columns[2].replace(".", "").replace(",", "."))));
        }
        return wknPoints;
    }

    @Override
    public List<AssetBuy> readBuys() throws IOException {
        ArrayList<AssetBuy> assetBuys = new ArrayList<>();
        String file = ResourceFileReader.readResource("assetBuys");
        String[] buys = file.split("\n");
        for (String buy : buys) {
            String[] buyData = buy.split(",");
            System.out.println(buy);
            String wkn = buyData[0];
            LocalDate date = LocalDate.parse(buyData[1]);
            int amount = Integer.parseInt(buyData[2]);
            double fee = Double.parseDouble(buyData[3]);
            double value = Double.parseDouble(buyData[4]);
            LocalDate soldDate = !buyData[5].equals("null") ? LocalDate.parse(buyData[5]) : null;
            Double soldValue = !buyData[6].equals("null") ? Double.parseDouble(buyData[6]) : null;
            AssetBuy assetBuy = new AssetBuy(
                    wkn,
                    date,
                    amount,
                    fee,
                    value,
                    soldDate,
                    soldValue
            );
            assetBuys.add(assetBuy);
        }
        return assetBuys;
    }

    @Override
    public String getWknName(String wkn) throws IOException {
        String[] lines = getWknFileLines(wkn);
        return lines[0];
    }

    @Override
    public String getWknType(String wkn) throws IOException {
        String[] lines = getWknFileLines(wkn);
        return lines[1];
    }

    @Override
    public String[] readWatchTypes() throws IOException {
        String file = ResourceFileReader.readResource("watchType");
        return file.split("\n");
    }

    @Override
    public Integer readCash() throws IOException {
        return readMoneyFile("cash");
    }

    @Override
    public Integer readBank() throws IOException {
        return readMoneyFile("bank");
    }

    private Integer readMoneyFile(String cash) throws IOException {
        int cashSum = 0;
        int cashSum1 = cashSum;
        String cashFile = ResourceFileReader.readResource(cash);
        String[] entries = cashFile.split("\n");
        for (String entry : entries) {
            cashSum1 += Integer.parseInt(entry.split(" ")[0]);
        }
        cashSum = cashSum1;
        return cashSum;
    }

    @Override
    public String[] getAllWkns() throws ResourceNotFound {
        return ResourceFileReader.getFilenamesInResourceFolder("wkn");
    }

    private String[] getWknFileLines(String wkn) throws IOException {
        String resource = ResourceFileReader.readResource("wkn/" + wkn);
        return resource.split("\n");
    }
}
