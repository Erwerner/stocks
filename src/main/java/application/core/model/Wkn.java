package application.core.model;

public class Wkn {
    private final String wkn;
    private final String wknName;
    private final String wknType;
    private final String wknUrl;

    public String getWkn() {
        return wkn;
    }

    public String getWknName() {
        return wknName;
    }

    public String getWknType() {
        return wknType;
    }

    public String getWknUrl() {
        return wknUrl;
    }

    public Wkn(String wkn, String wknName, String wknType, String wknUrl) {

        this.wkn = wkn;
        this.wknName = wknName;
        this.wknType = wknType;
        this.wknUrl = wknUrl;
    }
}
