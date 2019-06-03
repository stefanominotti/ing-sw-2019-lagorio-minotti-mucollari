package it.polimi.se2019.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class RegistrationFormApp extends Application {

    private static final String PATH = "utils/style/fxml/";
    //private List<String> names = new ArrayList<>();
    Stage window;


    public void Main(String[] args) { launch(args); }

    @Override
    public void start(Stage window) throws Exception {
        RegistrationFormController controller = new RegistrationFormController();

        Parent selectNickname = FXMLLoader.load(getClass().getClassLoader().getResource(PATH + "SelectNickname.fxml"));
        Parent selectCharacter = FXMLLoader.load(getClass().getClassLoader().getResource(PATH + "SelectCharacter.fxml"));
        window.setTitle("Adrenaline - Sign up");
        window.setScene(new Scene(selectCharacter));
        window.show();
    }
}


