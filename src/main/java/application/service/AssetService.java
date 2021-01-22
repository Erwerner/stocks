package application.service;

import application.core.model.ApplicationData;
import application.core.model.Asset;
import application.core.model.Wkn;
import application.core.model.exception.DateNotFound;

import java.time.LocalDate;

public class AssetService {
    public double calcAssetChangeToday(Asset asset, LocalDate date) {
        Double old = 1.0;
        Double neu = 1.0;
        try {
            old = asset.getWknPointAtDate(date.minusDays(1));
            neu = asset.getWknPointAtDate(date);
        } catch (DateNotFound dateNotFound) {
            dateNotFound.printStackTrace();
        }
        return neu / old - 1;
    }

    public Wkn createWkn(String wkn, ApplicationData data) {
        return new Wkn(wkn, data.getWknName(wkn), data.getWknType(wkn), data.getWknUrl(wkn));
    }
}
