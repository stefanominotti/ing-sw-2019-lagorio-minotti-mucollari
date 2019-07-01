package it.polimi.se2019.view;

import it.polimi.se2019.controller.ActionType;
import it.polimi.se2019.model.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class for handling board controller
 */
public class BoardController extends AbstractSceneController {

    private static final int KILLSHOT_POINTS_SIZE = 6;
    private static final String DROPS_PATH = "drops/";
    private static final String UTILS_PATH = "utils/icons/";
    private static final String CHARACTERS_ICONS_PATH = "utils/icons/characters_icon/";
    private static final String CHARACTERS_PATH = "characters/";
    private static final String PLAYER_BOARDS_PATH = "player_boards/";
    private static final String WEAPONS_PATH = "weapons/img/";
    private static final String POWERUPS_PATH = "powerups/";
    private static final String ARENAS_PATH = "arenas/img/";
    private static final String AMMO_TILES_PATH = "ammotiles/img/";

    private GameCharacter activeBoard;
    private Pane arenaPane;
    private List<ImageView> players;

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
    @FXML
    private ImageView arenaImage;
    @FXML
    private Pane arenaContainer;
    @FXML
    private Pane arena1Pane;
    @FXML
    private Pane arena2Pane;
    @FXML
    private Pane arena3Pane;
    @FXML
    private Pane arena4Pane;
    @FXML
    private Text messagesText;
    @FXML
    private Label currentStatusLabel;
    @FXML
    private Label currentActionLabel;
    @FXML
    private GridPane actionsPane;
    @FXML
    private GridPane arenaButtonsContainer;

    private EventHandler<MouseEvent> setPlayerBoardHandler;
    private EventHandler<MouseEvent> weaponInfoHandler;
    private EventHandler<MouseEvent> actionSelectionHandler;
    private EventHandler<MouseEvent> powerupSelectionHandler;
    private EventHandler<MouseEvent> squareSelectionHandler;

    public BoardController() {
        this.setPlayerBoardHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setPlayerBoard(GameCharacter.valueOf(((ImageView) event.getSource()).getId().toUpperCase()));
            }
        };
        this.weaponInfoHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Weapon weapon = Weapon.valueOf(((ImageView) event.getSource()).getId().toUpperCase());
                new Thread(() -> getView().showWeaponInfo(weapon)).start();
            }
        };
        this.actionSelectionHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ActionType action = ActionType.valueOf(((Button) event.getSource()).getId().toUpperCase());
                getView().handleActionInput(action);
            }
        };
        this.powerupSelectionHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String[] splitId = ((ImageView) event.getSource()).getId().split("_");
                String typeString = String.join("_", Arrays.copyOfRange(splitId, 1, splitId.length-1));
                String colorString = splitId[splitId.length-1];
                PowerupType type = PowerupType.valueOf(typeString.toUpperCase());
                AmmoType color = AmmoType.valueOf(colorString.toUpperCase());
                getView().handlePowerupInput(type, color);
            }
        };
        this.squareSelectionHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Button s = (Button) event.getSource();
                int x = s.getId().charAt(s.getId().length()-2);
                int y = s.getId().charAt(s.getId().length()-1);
                getView().handleSquareInput(x, y);
            }
        };
    }

    @FXML
    void setPlayerBoard() {
        setPlayerBoard(getView().getCharacter());
    }

    GameCharacter getActiveBoard() {
        return this.activeBoard;
    }

    void disableBoardChange() {
        setPlayerBoard(getView().getCharacter());
        for (ImageView p : this.players) {
            Platform.runLater(() ->
                    p.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.setPlayerBoardHandler)
            );
        }
    }

    void enableBoardChange() {
        for (ImageView p : this.players) {
            Platform.runLater(() ->
                p.setOnMousePressed(this.setPlayerBoardHandler)
            );
        }
    }

    void setArena() {
        Platform.runLater(() ->
                this.arenaImage.setImage(new Image(ARENAS_PATH + "arena_" + getView().getBoard().getArena() +
                        ".png"))
        );
        switch(getView().getBoard().getArena()) {
            case 1:
                this.arenaPane = this.arena1Pane;
                Platform.runLater(() -> {
                    this.arenaContainer.getChildren().remove(this.arena2Pane);
                    this.arenaContainer.getChildren().remove(this.arena3Pane);
                    this.arenaContainer.getChildren().remove(this.arena4Pane);
                });
                break;
            case 2:
                this.arenaPane = this.arena2Pane;
                Platform.runLater(() -> {
                    this.arenaContainer.getChildren().remove(this.arena1Pane);
                    this.arenaContainer.getChildren().remove(this.arena3Pane);
                    this.arenaContainer.getChildren().remove(this.arena4Pane);
                });
                break;
            case 3:
                this.arenaPane = this.arena3Pane;
                Platform.runLater(() -> {
                    this.arenaContainer.getChildren().remove(this.arena1Pane);
                    this.arenaContainer.getChildren().remove(this.arena2Pane);
                    this.arenaContainer.getChildren().remove(this.arena4Pane);
                });
                break;
            case 4:
                this.arenaPane = this.arena4Pane;
                Platform.runLater(() -> {
                    this.arenaContainer.getChildren().remove(this.arena1Pane);
                    this.arenaContainer.getChildren().remove(this.arena2Pane);
                    this.arenaContainer.getChildren().remove(this.arena3Pane);
                });
                break;
            default:
                break;
        }
        updateKillshotTrack();
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

    void setPlayerBoard(GameCharacter player) {
        this.activeBoard = player;
        PlayerBoard board;
        String labelText;
        if (player == getView().getCharacter()) {
            board = getView().getSelfPlayerBoard();
            labelText = "Your board";
        } else {
            board = getView().getBoardByCharacter(player);
            labelText = player.toString();
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
            this.pointsVBox.setVisible(true);
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
            Platform.runLater(() ->
                img.setImage(new Image(DROPS_PATH + "drop_" + c.getColor() + ".png"))
            );
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
            Platform.runLater(() ->
                img.setImage(new Image(DROPS_PATH + "drop_" + c.getColor() + ".png"))
            );
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
            Platform.runLater(() ->
                img.setImage(new Image(UTILS_PATH + "skull.png"))
            );
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
        Platform.runLater(() ->
            this.ammoLabel.setText(labelText)
        );
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
                    img.setId(w.toString().toLowerCase());
                    img.setImage(new Image(WEAPONS_PATH + w.toString().toLowerCase() + ".png"));
                    img.setOnMousePressed(this.weaponInfoHandler);
                });
                i++;
            }
            while (i < 3) {
                ImageView img = (ImageView) grid.getChildren().get(i);
                Platform.runLater(() -> {
                    img.setId(null);
                    img.setImage(null);
                    img.setOnMousePressed(this.weaponInfoHandler);
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
                img.setId(w.toString().toLowerCase());
                img.setImage(new Image(WEAPONS_PATH + w.toString().toLowerCase()));
                stateImg.setImage(new Image(UTILS_PATH + "unloaded_weapon.png"));
                img.setOnMousePressed(this.weaponInfoHandler);
            });
            i++;
        }
        if (this.activeBoard != getView().getCharacter()) {
            while (i < board.getWeaponsNumber() + 3) {
                ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
                ImageView stateImg = (ImageView) this.playerAssetsGrid.getChildren().get(i + 6);
                Platform.runLater(() -> {
                    img.setId(null);
                    img.setImage(new Image(WEAPONS_PATH + "weapon_back.png"));
                    stateImg.setImage(null);
                    img.setOnMousePressed(this.weaponInfoHandler);
                });
                i++;
            }
        } else {
            for (Weapon w : getView().getSelfPlayerBoard().getReadyWeapons()) {
                ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
                ImageView stateImg = (ImageView) this.playerAssetsGrid.getChildren().get(i + 6);
                Platform.runLater(() -> {
                    img.setId(w.toString().toLowerCase());
                    img.setImage(new Image(WEAPONS_PATH + w.toString().toLowerCase()));
                    stateImg.setImage(new Image(UTILS_PATH + "weapon_ready.png"));
                    img.setOnMousePressed(this.weaponInfoHandler);
                });
                i++;
            }
        }
        while (i < 6) {
            ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
            ImageView stateImg = (ImageView) this.playerAssetsGrid.getChildren().get(i + 6);
            Platform.runLater(() -> {
                img.setId(null);
                img.setImage(null);
                stateImg.setImage(null);
                img.setOnMousePressed(this.weaponInfoHandler);
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
                    img.setId(null);
                });
                i++;
            }
        } else {
            for (Powerup p : getView().getSelfPlayerBoard().getPowerups()) {
                ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
                int index = i - 5;
                Platform.runLater(() -> {
                    img.setImage(new Image(POWERUPS_PATH + p.getType().toString().toLowerCase() + "_" +
                            p.getColor().toString().toLowerCase() + ".png"));
                    img.setId(index + "_" + p.getType().toString().toLowerCase() + "_" +
                            p.getColor().toString().toLowerCase());
                });
                i++;
            }
        }
        while (i < 9) {
            ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
            Platform.runLater(() -> {
                img.setImage(null);
                img.setId(null);
            });
            i++;
        }
    }

    GridPane getSquarePaneByCoordinates(int x, int y) {
        int arena = getView().getBoard().getArena();
        for (Node n : this.arenaPane.getChildren()) {
            if (n.getId().equalsIgnoreCase("arena" + arena + x + y)) {
                return (GridPane) n;
            }
        }
        return null;
    }

    Button getSquareButtonByCoordinates(int x, int y) {
        for (Node n : this.arenaPane.getChildren()) {
            if (n.getId().equalsIgnoreCase("s" + x + y)) {
                return (Button) n;
            }
        }
        return null;
    }

    void updateTiles() {
        for (SquareView s : getView().getBoard().getSquares()) {
            if (s.isSpawn()) {
                continue;
            }
            ImageView img = (ImageView) getSquarePaneByCoordinates(s.getX(), s.getY())
                    .getChildren().get(getSquarePaneByCoordinates(s.getX(), s.getY()).getChildren().size() - 1);
            if (s.getAvailableAmmoTile() == null) {
                Platform.runLater(() ->
                        img.setImage(null)
                );
            } else {
                Platform.runLater(() ->
                        img.setImage(new Image(AMMO_TILES_PATH + "ammotile_" + s.getAvailableAmmoTile().getNumber() +
                                ".png"))
                );
            }
        }
    }

    void updatePlayersPositions() {
        this.players = new ArrayList<>();
        for (SquareView s : getView().getBoard().getSquares()) {
            GridPane squarePane = getSquarePaneByCoordinates(s.getX(), s.getY());
            if (s.getActivePlayers().isEmpty()) {
                for (int j = 0; j < 5; j++) {
                    ImageView img = (ImageView) squarePane.getChildren().get(j);
                    Platform.runLater(() -> {
                        img.setImage(null);
                        img.setId(null);
                        img.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.setPlayerBoardHandler);
                    });
                }
                continue;
            }
            int i = 0;
            for (GameCharacter c : s.getActivePlayers()) {
                ImageView img = (ImageView) squarePane.getChildren().get(i);
                this.players.add(img);
                Platform.runLater(() -> {
                    img.setImage(new Image(CHARACTERS_PATH + c.toString().toLowerCase() + ".png"));
                    img.setId(c.toString().toLowerCase());
                    img.setOnMousePressed(this.setPlayerBoardHandler);
                });
                i++;
            }
        }
    }

    void showMessage(List<String> messages) {
        String message = messages.stream().collect(Collectors.joining("\n"));
        Platform.runLater(() ->
                this.messagesText.setText(message)
        );
    }

    void setBanner(String status, String action) {
        Platform.runLater(() -> {
            this.currentStatusLabel.setText(status);
            this.currentActionLabel.setText(action);
        });
    }

    void setActions(List<ActionType> actions) {
        Button b = (Button) this.actionsPane.getChildren().get(0);
        Platform.runLater(() -> {
            if (actions.contains(ActionType.CANCEL)) {
                b.setDisable(false);
                b.setText("Cancel");
                b.setOnMousePressed(this.actionSelectionHandler);
                b.setId("cancel");
            } else {
                b.setDisable(false);
                b.setText("End Turn");
                b.setOnMousePressed(this.actionSelectionHandler);
                b.setId("endturn");
            }
        });
        for (Node n : this.actionsPane.getChildren()) {
            Platform.runLater(() -> {
                if (actions.contains(ActionType.valueOf(n.getId().toUpperCase()))) {
                    n.setDisable(false);
                    n.setOnMousePressed(this.actionSelectionHandler);
                } else {
                    n.setDisable(true);
                    n.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.actionSelectionHandler);
                }
            });
        }
    }

    void setPowerups(List<Powerup> powerups) {
        Platform.runLater(() -> {
            for (int i = 6; i < 9; i++) {
                ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
                if (img.getId() == null) {
                    continue;
                }
                String[] splitId = img.getId().split("_");
                String typeString = String.join("_", Arrays.copyOfRange(splitId, 1, splitId.length-1));
                String colorString = splitId[splitId.length-1];
                PowerupType type = PowerupType.valueOf(typeString.toUpperCase());
                AmmoType color = AmmoType.valueOf(colorString.toUpperCase());
                for (Powerup p : powerups) {
                    if (p.getType() == type && p.getColor() == color) {
                        img.setOnMousePressed(this.powerupSelectionHandler);
                    } else {
                        img.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.powerupSelectionHandler);
                    }
                }
            }
        });
    }

    void setSquares(List<Coordinates> coordinates) {
        for (Node s : this.arenaButtonsContainer.getChildren()) {
            boolean valid = false;
            for (Coordinates c : coordinates) {
                if (s.getId().equalsIgnoreCase("s" + c.getX() + c.getY())) {
                    Platform.runLater(() -> {
                        s.setOnMousePressed(this.squareSelectionHandler);
                        s.toFront();
                        s.setVisible(true);
                    });
                    valid = true;
                }
            }
            if (!valid) {
                Platform.runLater(() -> {
                    s.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.squareSelectionHandler);
                    s.setVisible(false);
                    s.toBack();
                });
            }
        }
    }
}


