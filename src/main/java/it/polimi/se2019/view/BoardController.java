package it.polimi.se2019.view;

import it.polimi.se2019.model.AmmoType;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Powerup;
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
    private static final String POWERUPS_PATH = "powerups/img/";

    private GameCharacter activeBoard;

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
    @FXML
    private GridPane playerAssetsGrid;

    public GameCharacter getActiveBoard() {
        return this.activeBoard;
    }

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

    @FXML
    private void setPlayerBoard() {
        setPlayerBoard(GameCharacter.VIOLET);
    }

    void setPlayerBoard(GameCharacter player) {
        this.activeBoard = player;
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
        updateBoardMarks();
        updateBoardDamages();
        updateKillshotPoints();
        updateAmmo();
        if (player != getView().getCharacter()) {
            this.pointsVBox.setVisible(false);
        } else {
            updatePoints();
        }
        updateWeapons();
        updatePowerups();
    }

    void updateBoardMarks() {
        PlayerBoard board;
        if (this.activeBoard == getView().getCharacter()) {
            board = getView().getSelfPlayerBoard();
        } else {
            board = getView().getBoardByCharacter(this.activeBoard);
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

    void updateBoardDamages() {
        PlayerBoard board;
        if (this.activeBoard == getView().getCharacter()) {
            board = getView().getSelfPlayerBoard();
        } else {
            board = getView().getBoardByCharacter(this.activeBoard);
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

    void updateKillshotPoints() {
        PlayerBoard board;
        if (this.activeBoard == getView().getCharacter()) {
            board = getView().getSelfPlayerBoard();
        } else {
            board = getView().getBoardByCharacter(this.activeBoard);
        }
        for (int i = 0; i < KILLSHOT_POINTS_SIZE - board.getKillshotPoints().size(); i++) {
            ImageView img = (ImageView) this.deathsGrid.getChildren().get(i);
            Platform.runLater(() -> {
                img.setImage(new Image(UTILS_PATH + "skull.png"));
            });
        }
    }

    void updateAmmo() {
        PlayerBoard board;
        String labelText;
        if (this.activeBoard == getView().getCharacter()) {
            board = getView().getSelfPlayerBoard();
            labelText = "Your ammo";
        } else {
            board = getView().getBoardByCharacter(this.activeBoard);
            labelText = this.activeBoard + "'s ammo";
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
                    img.setImage(new Image(WEAPONS_PATH + w.toString().toLowerCase() + ".png"));
                });
                i++;
            }
        }
    }

    void updateWeapons() {
        int i = 3;
        PlayerBoard board;
        if (this.activeBoard == getView().getCharacter()) {
            board = getView().getSelfPlayerBoard();
        } else {
            board = getView().getBoardByCharacter(this.activeBoard);
        }
        for (Weapon w : board.getUnloadedWeapons()) {
            ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
            ImageView stateImg = (ImageView) this.playerAssetsGrid.getChildren().get(i + 6);
            Platform.runLater(() -> {
                img.setImage(new Image(WEAPONS_PATH + w.toString().toLowerCase()));
                stateImg.setImage(new Image(UTILS_PATH + "unloaded_weapon.png"));
            });
            i++;
        }
        if (this.activeBoard != getView().getCharacter()) {
            while (i < board.getWeaponsNumber() + 3) {
                ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
                ImageView stateImg = (ImageView) this.playerAssetsGrid.getChildren().get(i + 6);
                Platform.runLater(() -> {
                    img.setImage(new Image(WEAPONS_PATH + "weapon_back.png"));
                    stateImg.setImage(null);
                });
                i++;
            }
        } else {
            for (Weapon w : getView().getSelfPlayerBoard().getReadyWeapons()) {
                ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
                ImageView stateImg = (ImageView) this.playerAssetsGrid.getChildren().get(i + 6);
                Platform.runLater(() -> {
                    img.setImage(new Image(WEAPONS_PATH + w.toString().toLowerCase()));
                    stateImg.setImage(new Image(UTILS_PATH + "weapon_ready.png"));
                });
                i++;
            }
        }
        while (i < 6) {
            ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
            ImageView stateImg = (ImageView) this.playerAssetsGrid.getChildren().get(i + 6);
            Platform.runLater(() -> {
                img.setImage(null);
                stateImg.setImage(null);
            });
            i++;
        }
    }

    void updatePowerups() {
        int i = 6;
        if (this.activeBoard != getView().getCharacter()) {
            PlayerBoard board = getView().getBoardByCharacter(this.activeBoard);
            while (i < board.getPowerupsNumber() + 6) {
                ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
                Platform.runLater(() -> {
                    img.setImage(new Image(POWERUPS_PATH + "powerup_back.png"));
                });
                i++;
            }
        } else {
            for (Powerup p : getView().getSelfPlayerBoard().getPowerups()) {
                ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
                Platform.runLater(() -> {
                    img.setImage(new Image(POWERUPS_PATH + p.getType().toString().toLowerCase() +
                            p.getColor().toString().toLowerCase()));
                });
                i++;
            }
        }
        while (i < 9) {
            ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
            Platform.runLater(() -> {
                img.setImage(null);
            });
            i++;
        }
    }
}


