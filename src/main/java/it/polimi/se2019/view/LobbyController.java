package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.EnumMap;
import java.util.Map;


public class LobbyController extends AbstractSceneController {

    private int lastIndex;
    private Map<GameCharacter, HBox> playerBoxes;

    @FXML
    private VBox playersList;
    @FXML
    private Label messageName;
    @FXML
    private ImageView messageImg;

    public LobbyController() {
        this.lastIndex = 0;
        this.playerBoxes = new EnumMap<>(GameCharacter.class);
    }

    void setPlayers(Map<GameCharacter, String> players) {
       int index = 0;
       for(Map.Entry<GameCharacter, String> player : players.entrySet()) {

           HBox playerBox = (HBox) this.playersList.getChildren().get(index);
           Label label = (Label) playerBox.getChildren().get(1);
           ImageView img = (ImageView) playerBox.getChildren().get(0);

           this.playerBoxes.put(player.getKey(), playerBox);
           Platform.runLater(() -> {
               label.setText(player.getValue());
               img.setImage(new Image("utils/icons/characters_icon/" + player.getKey().toString().toLowerCase() + ".png"));
           });
           index++;
       }
       this.lastIndex = index;
    }

    void addPlayer(GameCharacter character, String nickname) {

        HBox playerBox = (HBox) this.playersList.getChildren().get(this.lastIndex);
        Label label = (Label) playerBox.getChildren().get(1);
        ImageView img = (ImageView) playerBox.getChildren().get(0);

        this.playerBoxes.put(character, playerBox);
        Platform.runLater(() -> {
            label.setText(nickname);
            img.setImage(new Image("utils/icons/characters_icon/" + character.toString().toLowerCase() + ".png"));
        });
        this.lastIndex++;
    }

    void removePlayer(GameCharacter character) {
        HBox playerBox = this.playerBoxes.get(character);
        Label label = (Label) playerBox.getChildren().get(1);
        ImageView img = (ImageView) playerBox.getChildren().get(0);
        this.playerBoxes.remove(character);
        this.lastIndex--;
        Platform.runLater(() -> {
            label.setText("");
            img.setImage(null);
            for (int j=0; j<5; j++) {
                int i = j;
                while (((Label) ((HBox) this.playersList.getChildren().get(i)).getChildren().get(1)).getText().equals("")) {
                    if (i == 4) {
                        break;
                    }
                    ImageView currentImage =
                            (ImageView) ((HBox) this.playersList.getChildren().get(i)).getChildren().get(0);
                    Label currentLabel = (Label) ((HBox) this.playersList.getChildren().get(i)).getChildren().get(1);

                    ImageView newImage = (ImageView) ((HBox) this.playersList.getChildren().get(i + 1)).getChildren().get(0);
                    Label newLabel = (Label) ((HBox) this.playersList.getChildren().get(i + 1)).getChildren().get(1);

                    currentImage.setImage(newImage.getImage());
                    currentLabel.setText(newLabel.getText());

                    newImage.setImage(null);
                    newLabel.setText("");

                    i++;
                }
            }

        });
    }

    void setMessage(String img, String text) {
        Platform.runLater(() -> {
            this.messageName.setText(text);
            this.messageImg.setImage(new Image("utils/icons/" + img));
        });
    }

}
