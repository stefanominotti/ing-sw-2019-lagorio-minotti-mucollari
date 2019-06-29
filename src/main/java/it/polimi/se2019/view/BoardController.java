package it.polimi.se2019.view;

import it.polimi.se2019.model.AmmoType;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Weapon;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

/**
 * Class for handling board controller
 */
public class BoardController extends AbstractSceneController {

    private static final int KILLSHOT_POINTS_SIZE = 6;
    private static final String DROPS_PATH = "drops/";
    private static final String UTILS_PATH = "utils/icons/";
    private static final String CHARACTERS_ICONS_PATH = "utils/icons/characters_icon/";
    private static final String PLAYER_BOARDS_PATH = "player_boards/";
    private static final String WEAPONS_PATH = "weapons/img/";

    @FXML
    private GridPane skullsGrid;

    @FXML
    private ImageView playerBoardImage;

    @FXML
    private ImageView characterBoardImage;

    @FXML
    private GridPane marksGrid;

    @FXML
    private GridPane damagesGrid;

    @FXML
    private GridPane deathsGrid;

    @FXML
    private Label characterBoardLabel;

    @FXML
    private Label redAmmoQty;

    @FXML
    private Label blueAmmoQty;

    @FXML
    private Label yellowAmmoQty;

    @FXML
    private Label ammoLabel;

    @FXML
    private VBox pointsVBox;

    @FXML
    private Label onePointQty;

    @FXML
    private Label twoPointQty;

    @FXML
    private Label fourPointQty;

    @FXML
    private GridPane blueWeaponsGrid;

    @FXML
    private GridPane redWeaponsGrid;

    @FXML
    private GridPane yellowWeaponsGrid;

    void updateKillshotTrack() {
        Map<Integer, List<GameCharacter>> killshotTrack = getView().getBoard().getKillshotTrack();
        for (Map.Entry<Integer, List<GameCharacter>> skull : killshotTrack.entrySet()) {
            ImageView img = (ImageView) this.skullsGrid.getChildren().get(killshotTrack.size() - skull.getKey());
            Platform.runLater(() -> {
                if (skull.getValue().isEmpty()) {
                    img.setImage(new Image(UTILS_PATH + "skull.png"));
                } else if (skull.getValue().size() == 1) {
                    img.setImage(new Image(DROPS_PATH + "drop_" + skull.getValue().get(0).getColor() + ".png"));
                } else if (skull.getValue().size() == 2) {
                    img.setImage(new Image(DROPS_PATH + "drop_" + skull.getValue().get(0).getColor() + "_" +
                            skull.getValue().get(1).getColor() + ".png"));
                }
            });
        }
    }

    void setPlayerBoard(GameCharacter player) {
        PlayerBoard board;
        String labelText;
        if (player == getView().getCharacter()) {
            board = getView().getSelfPlayerBoard();
            labelText = "Your board";
        } else {
            board = getView().getBoardByCharacter(player);
            labelText = player + "'s board";
        }
        Platform.runLater(() -> {
            if (!board.isFrenzyBoard()) {
                this.playerBoardImage.setImage(new Image(PLAYER_BOARDS_PATH + "player_board_" +
                        player.toString().toLowerCase() + ".png"));
            } else {
                this.playerBoardImage.setImage(new Image(PLAYER_BOARDS_PATH + "player_board_ff_" +
                        player.toString().toLowerCase() + ".png"));
            }
            this.characterBoardLabel.setText(labelText);
            this.characterBoardImage.setImage(new Image(CHARACTERS_ICONS_PATH + player.toString().toLowerCase() +
                    ".png"));
        });
        updateBoardMarks(player);
        updateBoardDamages(player);
        updateKillshotPoints(player);
        updateAmmo(player);
        if (player != getView().getCharacter()) {
            this.pointsVBox.setVisible(false);
        } else {
            updatePoints();
        }
    }

    void updateBoardMarks(GameCharacter player) {
        PlayerBoard board;
        if (player == getView().getCharacter()) {
            board = getView().getSelfPlayerBoard();
        } else {
            board = getView().getBoardByCharacter(player);
        }
        int i = 0;
        for (GameCharacter c : board.getRevengeMarks()) {
            ImageView img = (ImageView) this.marksGrid.getChildren().get(i);
            Platform.runLater(() -> {
                img.setImage(new Image(DROPS_PATH + "drop_" + c.getColor() + ".png"));
            });
            i++;
        }
    }

    void updateBoardDamages(GameCharacter player) {
        PlayerBoard board;
        if (player == getView().getCharacter()) {
            board = getView().getSelfPlayerBoard();
        } else {
            board = getView().getBoardByCharacter(player);
        }
        int i = 0;
        for (GameCharacter c : board.getDamages()) {
            ImageView img = (ImageView) this.damagesGrid.getChildren().get(i);
            Platform.runLater(() -> {
                img.setImage(new Image(DROPS_PATH + "drop_" + c.getColor() + ".png"));
            });
            i++;
        }
    }

    void updateKillshotPoints(GameCharacter player) {
        PlayerBoard board;
        if (player == getView().getCharacter()) {
            board = getView().getSelfPlayerBoard();
        } else {
            board = getView().getBoardByCharacter(player);
        }
        for (int i = 0; i < KILLSHOT_POINTS_SIZE - board.getKillshotPoints().size(); i++) {
            ImageView img = (ImageView) this.deathsGrid.getChildren().get(i);
            Platform.runLater(() -> {
                img.setImage(new Image(UTILS_PATH + "skull.png"));
            });
        }
    }

    void updateAmmo(GameCharacter player) {
        PlayerBoard board;
        String labelText;
        if (player == getView().getCharacter()) {
            board = getView().getSelfPlayerBoard();
            labelText = "Your ammo";
        } else {
            board = getView().getBoardByCharacter(player);
            labelText = player + "'s ammo";
        }
        Platform.runLater(() -> {
            this.ammoLabel.setText(labelText);
        });
        for (Map.Entry<AmmoType, Integer> ammo : board.getAvailableAmmos().entrySet()) {
            Platform.runLater(() -> {
                switch (ammo.getKey()) {
                    case BLUE:
                        this.blueAmmoQty.setText("x" + ammo.getValue());
                        break;
                    case RED:
                        this.redAmmoQty.setText("x" + ammo.getValue());
                        break;
                    case YELLOW:
                        this.yellowAmmoQty.setText("x" + ammo.getValue());
                        break;
                }
            });
        }
    }

    void updatePoints() {
        SelfPlayerBoard board = getView().getSelfPlayerBoard();
        int score = board.getScore();
        int fourPoints = score/4;
        score = fourPoints%4;
        int twoPoints = score/2;
        int onePoints = twoPoints%2;
        Platform.runLater(() -> {
            this.fourPointQty.setText("x" + fourPoints);
            this.twoPointQty.setText("x" + twoPoints);
            this.onePointQty.setText("x" + onePoints);
        });
    }

    void updateStores() {
        for (SquareView s : getView().getBoard().getSquares()) {
            if (!s.isSpawn()) {
                continue;
            }
            GridPane grid;
            switch (s.getColor()) {
                case RED:
                    grid = this.redWeaponsGrid;
                    break;
                case BLUE:
                    grid = this.blueWeaponsGrid;
                    break;
                case YELLOW:
                    grid = this.yellowWeaponsGrid;
                    break;
                default:
                    grid = null;
                    break;
            }
            if (grid == null) {
                return;
            }
            int i = 0;
            for (Weapon w : s.getStore()) {
                ImageView img = (ImageView) grid.getChildren().get(i);
                Platform.runLater(() -> {
                    img.setImage(new Image(WEAPONS_PATH + w.toString().toLowerCase()));
                });
                i++;
            }
        }
    }
}


