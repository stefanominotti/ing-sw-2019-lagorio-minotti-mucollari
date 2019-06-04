package it.polimi.se2019.view;

public abstract class AbstractSceneController {

    private GUIView view;

    void setView(GUIView view) {
        this.view = view;
    }

    GUIView getView() {
        return this.view;
    }
}
