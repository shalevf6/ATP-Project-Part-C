package View;

/**
 * An interface to represent a displayer layer in the game
 */
public interface Displayer {

    /**
     * Redraws the displayer using the given objects
     * @param objects - given objects
     */
    void redraw(Object... objects);

    /**
     * Resets the displayers zoom using given coordinates
     * @param x - a given x coordinate
     * @param y - a given y coordinate
     */
    void ResetZooming(double x,double y);
}
