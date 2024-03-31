package squeek.appleskin.common;

/**
 * double config item
 *
 * @author luckylamball
 */
public class DoubleConfiguration implements ConfigurationItem<Double> {

    /**
     * config value
     */
    private double value;

    /**
     * min value
     */
    private double minValue;

    /**
     * max value
     */
    private double maxValue;

    /**
     * config name
     */
    private String name;

    /**
     * config meaning
     */
    private String comment;

    public DoubleConfiguration(double value, double minValue, double maxValue, String name, String comment) {
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.name = name;
        this.comment = comment;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public Double get() {
        return value;
    }

    public void set(double value) {
        this.value = value;
    }
}
