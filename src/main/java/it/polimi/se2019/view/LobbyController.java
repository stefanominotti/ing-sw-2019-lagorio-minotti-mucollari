package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Map;


public class LobbyController extends AbstractSceneController {

    private int lastIndex;



    @FXML
    private Label player1_name;
    @FXML
    private Label player2_name;
    @FXML
    private Label player3_name;
    @FXML
    private Label player4_name;
    @FXML
    private Label player5_name;

    @FXML
    private ImageView player1_img;
    @FXML
    private ImageView player2_img;
    @FXML
    private ImageView player3_img;
    @FXML
    private ImageView player4_img;
    @FXML
    private ImageView player5_img;

    @FXML
    private Label message_name;
    @FXML
    private ImageView message_img;

    public LobbyController(int lastIndex) {
        this.lastIndex = 1;
    }


    public void setPlayers(Map<GameCharacter, String> players) {
       int index = 1;
       for(Map.Entry<GameCharacter, String> player : players.entrySet()) {
           Label label;
           ImageView img;

           switch(index) {
               case 1:
                   label = this.player1_name;
                   img = this.player1_img;
                   break;
               case 2:
                   label = this.player2_name;
                   img = this.player2_img;
                   break;
               case 3:
                   label = this.player3_name;
                   img = this.player3_img;
                   break;
               case 4:
                   label = this.player4_name;
                   img = this.player4_img;
                   break;

               default:
                   label = this.player5_name;
                   img = this.player5_img;
                   break;
           }
           label.setText(player.getValue());
           img.setImage(new Image("utils/icons/characters_icons" + player.getKey().toString().toLowerCase() + ".png"));
           index++;
       }
   }

   

}
