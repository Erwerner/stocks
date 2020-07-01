package application.core;

public class Wkn {
    private String wkn;
    private String wknName;
    private String wknType;
    private String wknUrl;

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
