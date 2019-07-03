package it.polimi.se2019.view.guicontrollers;

import it.polimi.se2019.model.GameCharacter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Map;

/**
 * Class for handling ranking controller
 */
public class RankingController extends AbstractSceneController {

    private static final int MIN_PLAYERS = 3;
    private static final String CHARACTERS_ICON_PATH = "utils/icons/characters_icon/";

    @FXML
    private Label message;

    @FXML
    private ImageView firstIcon;
    @FXML
    private Label firstPoints;

    @FXML
    private ImageView secondIcon;
    @FXML
    private Label secondPoints;

    @FXML
    private ImageView thirdIcon;
    @FXML
    private Label thirdPoints;

    @FXML
    private Pane fourPlayerPane;
    @FXML
    private Pane fivePlayerPane;
    @FXML
    private ImageView fourthIcon;
    @FXML
    private Label fourthPoints;
    @FXML
    private ImageView fifthIcon;
    @FXML
    private Label fifthPoints;


    public void showMessage (Map<GameCharacter, Integer> ranking){
        buildRanking(ranking);
    }

    private void buildRanking(Map<GameCharacter, Integer> ranking) {
        if(ranking.size()==MIN_PLAYERS+1) {
            Platform.runLater(() -> {
                this.fourPlayerPane.setVisible(true);
                setImages(ranking);
            });
        }
        else if(ranking.size()==MIN_PLAYERS+2) {
            Platform.runLater(() -> {
                this.fourPlayerPane.setVisible(true);
                this.fivePlayerPane.setVisible(true);
                setImages(ranking);
            });
        }
        else {
            setImages(ranking);
        }

    }
    private void setImages(Map<GameCharacter, Integer> ranking) {
        Platform.runLater(() -> {
            int index = 1;
            for(Map.Entry<GameCharacter, Integer> rankingIndex : ranking.entrySet()) {
                switch (index){
                    case 1:
                        this.firstIcon.setImage(new Image(CHARACTERS_ICON_PATH + rankingIndex.getKey().toString()
                                .toLowerCase() + ".png"));
                        this.firstPoints.setText(rankingIndex.getValue().toString());
                        if (getView().getCharacter()==rankingIndex.getKey()) {
                            message.setText("You Win");
                        }
                        break;
                    case 2:
                        this.secondIcon.setImage(new Image(CHARACTERS_ICON_PATH + rankingIndex.getKey().toString()
                                .toLowerCase() + ".png"));
                        this.secondPoints.setText(rankingIndex.getValue().toString());
                        if (getView().getCharacter()==rankingIndex.getKey()) {
                            message.setText("Congratulation, you finished 2th");
                        }
                        break;
                    case 3:
                        this.thirdIcon.setImage(new Image(CHARACTERS_ICON_PATH + rankingIndex.getKey().toString()
                                .toLowerCase() + ".png"));
                        this.thirdPoints.setText(rankingIndex.getValue().toString());

                        if (getView().getCharacter()==rankingIndex.getKey()) {
                            message.setText("Not bad, you finished 3th");
                        }
                        break;
                    case 4:
                        this.fourthIcon.setImage(new Image(CHARACTERS_ICON_PATH + rankingIndex.getKey().toString()
                                .toLowerCase() + ".png"));
                        this.fourthPoints.setText(rankingIndex.getValue().toString());
                        if (getView().getCharacter()==rankingIndex.getKey()) {
                            message.setText("You finished 4th");
                        }
                        break;
                    case 5:
                        this.fifthIcon.setImage(new Image(CHARACTERS_ICON_PATH + rankingIndex.getKey().toString()
                                .toLowerCase() + ".png"));
                        this.fifthPoints.setText(rankingIndex.getValue().toString());
                        if (getView().getCharacter()==rankingIndex.getKey()) {
                            message.setText("You finished 5th");
                        }
                        break;
                    default:
                        break;
                }
            index++;
            }
        });
    }

    public void quitApp(){
        System.exit(0);
    }

}


