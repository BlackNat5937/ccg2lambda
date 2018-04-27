package visualization.controller;

/**
 * Enables passing data to controllers.
 *
 * @param <T> the type of the data to pass
 * @author Gaétan Basile
 */
public interface Parametrable<T> {
    /**
     * Initialises data the controller needs.
     *
     * @param data the data to pass
     */
    void initData(T data);
}
