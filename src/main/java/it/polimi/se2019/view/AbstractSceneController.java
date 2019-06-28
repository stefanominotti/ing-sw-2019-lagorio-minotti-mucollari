package it.polimi.se2019.view;

/**
 * Abstract class for handling abstract scene controller
 */
public abstract class AbstractSceneController {

    private GUIView view;

    /**
     * Sets a gui view
     * @param view you want to set
     */
    void setView(GUIView view) {
        this.view = view;
    }

    /**
     * Gets the current gui view
     * @return the current gui view
     */
    GUIView getView() {
        return this.view;
    }
}
