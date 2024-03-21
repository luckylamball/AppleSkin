package squeek.appleskin.common;

public class ConfigurationItem {

    // boolean config value
    private boolean defaultBoolean;

    // double config value(if exist)
    private double defaultDouble;

    // double value range
    private double minDouble;

    // double value range
    private double maxDouble;

    // config name
    private String name;

    // config meaning
    private String comment;

    public ConfigurationItem(boolean defaultBoolean, String name, String comment) {
        this(defaultBoolean, 0d, 0d, 0d, name, comment);
    }

    public ConfigurationItem(double defaultDouble, double minDouble, double maxDouble, String name, String comment) {
        this(true, defaultDouble, minDouble, maxDouble, name, comment);
    }

    public ConfigurationItem(boolean defaultBoolean, double defaultDouble, double minDouble, double maxDouble, String name, String comment) {
        this.defaultBoolean = defaultBoolean;
        this.defaultDouble = defaultDouble;
        this.minDouble = minDouble;
        this.maxDouble = maxDouble;
        this.name = name;
        this.comment = comment;
    }

    public boolean getDefaultBoolean() {
        return defaultBoolean;
    }

    public void setDefaultBoolean(boolean defaultBoolean) {
        this.defaultBoolean = defaultBoolean;
    }

    public double getDefaultDouble() {
        return defaultDouble;
    }

    public void setDefaultDouble(double defaultDouble) {
        this.defaultDouble = defaultDouble;
    }

    public double getMinDouble() {
        return minDouble;
    }

    public void setMinDouble(double minDouble) {
        this.minDouble = minDouble;
    }

    public double getMaxDouble() {
        return maxDouble;
    }

    public void setMaxDouble(double maxDouble) {
        this.maxDouble = maxDouble;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
