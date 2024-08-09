package charlesmercado0522.weatherapp.types;

public class Advisories {
    private String advisory;
    private String date;

    public Advisories(String advisory, String date) {
        this.advisory = advisory;
        this.date = date;
    }

    public String getAdvisory() {
        return advisory;
    }

    public String getDate() {
        return date;
    }
}
