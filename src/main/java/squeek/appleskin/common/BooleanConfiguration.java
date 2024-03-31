package squeek.appleskin.common;

/**
 * boolean config item
 *
 * @author luckylamball
 */
public class BooleanConfiguration implements ConfigurationItem<Boolean> {

    /**
     * config value
     */
    private boolean value;

    /**
     * config name
     */
    private String name;

    /**
     * config meaning
     */
    private String comment;

    public BooleanConfiguration(boolean value, String name, String comment) {
        this.value = value;
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
    public Boolean get() {
        return value;
    }

    public void set(boolean value) {
        this.value = value;
    }
}
