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

/**
 * Class for handling lobby cotroller
 */
public class LobbyController extends AbstractSceneController {

    private int lastIndex;
    private Map<GameCharacter, HBox> playerBoxes;

    @FXML
    private VBox playersList;
    @FXML
    private Label messageName;
    @FXML
    private ImageView messageImg;

    /**
     * Class constructor, it builds a lobby controller
     */
    public LobbyController() {
        this.lastIndex = 0;
        this.playerBoxes = new EnumMap<>(GameCharacter.class);
    }

    /**
     * Sets players on lobby
     * @param players map with characters and their nicknames to be set on the lobby
     */
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

    /**
     * Add a player to the lobby at runtime
     * @param character to be added
     * @param nickname to be added
     */
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

    /**
     * Removes a player from the lobby at runtime
     * @param character to be removed
     */
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

                    HBox currentBox = (HBox) this.playersList.getChildren().get(i);
                    ImageView currentImage =
                            (ImageView) currentBox.getChildren().get(0);
                    Label currentLabel = (Label) currentBox.getChildren().get(1);

                    HBox newBox = (HBox) this.playersList.getChildren().get(i + 1);
                    ImageView newImage = (ImageView) newBox.getChildren().get(0);
                    Label newLabel = (Label) newBox.getChildren().get(1);

                    currentImage.setImage(newImage.getImage());
                    currentLabel.setText(newLabel.getText());

                    newImage.setImage(null);
                    newLabel.setText("");

                    for (Map.Entry<GameCharacter, HBox> box : this.playerBoxes.entrySet()) {
                        if (box.getValue() == newBox) {
                            this.playerBoxes.put(box.getKey(), currentBox);
                            break;
                        }
                    }

                    i++;
                }
            }

        });
    }

    /**
     * Sets a message to the lobby
     * @param img name to be set with the message
     * @param text of the message
     */ 
    void setMessage(String img, String text) {
        Platform.runLater(() -> {
            this.messageName.setText(text);
            this.messageImg.setImage(new Image("utils/icons/" + img));
        });
    }

}
