package it.polimi.se2019.view.guicontrollers;

import it.polimi.se2019.view.GUIView;

/**
 * Abstract class for handling abstract scene controller
 *  @author antoniolagorio
 */
public abstract class AbstractSceneController {

    private GUIView view;

    /**
     * Sets a gui view
     * @param view you want to set
     */
    public void setView(GUIView view) {
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
