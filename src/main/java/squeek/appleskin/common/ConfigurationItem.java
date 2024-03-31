package squeek.appleskin.common;

/**
 * common config item interface
 *
 * @author luckylamball
 */
public interface ConfigurationItem<T> {

    /**
     * get current config name
     *
     * @return String
     */
    String getName();

    /**
     * get current config comment/meaning
     *
     * @return String
     */
    String getComment();

    /**
     * get current config value
     *
     * @return Object
     */
    T get();
}
