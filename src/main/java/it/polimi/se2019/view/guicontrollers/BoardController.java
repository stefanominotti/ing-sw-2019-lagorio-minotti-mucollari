package it.polimi.se2019.view.guicontrollers;

import it.polimi.se2019.controller.ActionType;
import it.polimi.se2019.model.*;
import it.polimi.se2019.model.arena.CardinalPoint;
import it.polimi.se2019.model.arena.Coordinates;
import it.polimi.se2019.model.playerassets.AmmoType;
import it.polimi.se2019.model.playerassets.Powerup;
import it.polimi.se2019.model.playerassets.PowerupType;
import it.polimi.se2019.model.playerassets.weapons.Weapon;
import it.polimi.se2019.model.playerassets.weapons.WeaponEffectOrderType;
import it.polimi.se2019.view.modelview.PlayerBoard;
import it.polimi.se2019.view.modelview.SelfPlayerBoard;
import it.polimi.se2019.view.modelview.SquareView;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for handling board controller
 */
public class BoardController extends AbstractSceneController {

    private static final int KILLSHOT_POINTS_SIZE = 6;
    private static final String FXML_PATH = "utils/style/fxml/";
    private static final String DROPS_PATH = "drops/";
    private static final String UTILS_PATH = "utils/icons/";
    private static final String CHARACTERS_ICONS_PATH = "utils/icons/characters_icon/";
    private static final String CHARACTERS_PATH = "characters/";
    private static final String PLAYER_BOARDS_PATH = "player_boards/";
    private static final String WEAPONS_PATH = "weapons/img/";
    private static final String POWERUPS_PATH = "powerups/img/";
    private static final String ARENAS_PATH = "arenas/img/";
    private static final String AMMO_TILES_PATH = "ammotiles/img/";

    private static final String CARD_SELECTABLE_CLASS = "card-selectable";
    private static final String CARD_SELECTED_CLASS = "card-selected";
    private static final String BUTTON_STD_CLASS = "button-std";
    private static final String BUTTON_DANGER_CLASS = "button-danger";
    private static final String BUTTON_CONFIRM_CLASS = "button-confirm";
    private static final String CHARACTER_CLASS = "img-characters";
    private static final String CHARACTER_SELECTABLE_CLASS = "img-characters-selectable";
    private static final String CHARACTER_SELECTED_CLASS = "img-characters-selected";

    private GameCharacter activeBoard;
    private Pane arenaPane;
    private List<ImageView> players;
    private Map<Weapon, ImageView> storeWeapons;

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
    private Text characterBoardLabel;
    @FXML
    private Label redAmmoQty;
    @FXML
    private Label blueAmmoQty;
    @FXML
    private Label yellowAmmoQty;
    @FXML
    private Label ammoLabel;
    @FXML
    private Label scoreLabel;
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
    private Text currentActionLabel;
    @FXML
    private GridPane actionsPane;
    @FXML
    private GridPane arenaButtonsContainer;
    @FXML
    private HBox secondaryButtonsBox;
    @FXML
    private GridPane extraMarks;

    private EventHandler<MouseEvent> setPlayerBoardHandler;
    private EventHandler<MouseEvent> weaponInfoHandler;
    private EventHandler<MouseEvent> actionSelectionHandler;
    private EventHandler<MouseEvent> powerupSelectionHandler;
    private EventHandler<MouseEvent> squareSelectionHandler;
    private EventHandler<MouseEvent> weaponSelectionHandler;
    private EventHandler<MouseEvent> confirmHandler;
    private EventHandler<MouseEvent> characterSelectionHandler;
    private EventHandler<MouseEvent> cardinalPointSelectionHandler;
    private EventHandler<MouseEvent> effectSelectionHandler;
    private EventHandler<MouseEvent> decisionSelectionHandler;
    private EventHandler<MouseEvent> ammosSelectionHandler;

    /**
     * Class constructor, it builds a board controller and its event handlers
     */
    public BoardController() {
        this.players = new ArrayList<>();
        this.storeWeapons = new EnumMap<>(Weapon.class);
        this.setPlayerBoardHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getSource() != null && ((ImageView) event.getSource()).getId() != null) {
                    new Thread(() ->
                            getView().setPlayerBoard(GameCharacter.valueOf(((ImageView) event.getSource()).getId()
                                    .toUpperCase()))).start();
                }
            }
        };
        this.weaponInfoHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getSource() != null && ((Node) event.getSource()).getId() != null) {
                    Weapon weapon = Weapon.valueOf(((ImageView) event.getSource()).getId().toUpperCase());
                    showWeaponInfo(weapon);
                }
            }
        };
        this.actionSelectionHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getSource() != null && ((Node) event.getSource()).getId() != null) {
                    ActionType action = ActionType.valueOf(((Button) event.getSource()).getId().toUpperCase());
                    getView().handleActionInput(action);
                }
            }
        };
        this.powerupSelectionHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getSource() != null && ((Node) event.getSource()).getId() != null) {
                    String[] splitId = ((ImageView) event.getSource()).getId().split("_");
                    String typeString = String.join("_", Arrays.copyOfRange(splitId, 1, splitId.length - 1));
                    String colorString = splitId[splitId.length - 1];
                    PowerupType type = PowerupType.valueOf(typeString.toUpperCase());
                    AmmoType color = AmmoType.valueOf(colorString.toUpperCase());
                    getView().handlePowerupInput(type, color);
                }
            }
        };
        this.squareSelectionHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getSource() != null && ((Node) event.getSource()).getId() != null) {
                    Button s = (Button) event.getSource();
                    int x = Integer.parseInt(String.valueOf(s.getId().charAt(s.getId().length() - 2)));
                    int y = Integer.parseInt(String.valueOf(s.getId().charAt(s.getId().length() - 1)));
                    getView().handleSquareInput(x, y);
                }
            }
        };
        this.weaponSelectionHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getSource() != null && ((Node) event.getSource()).getId() != null) {
                    ImageView s = (ImageView) event.getSource();
                    getView().handleWeaponInput(Weapon.valueOf(s.getId().toUpperCase()));
                }
            }
        };
        this.confirmHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getSource() != null && ((Node) event.getSource()).getId() != null) {
                    getView().handleContinue();
                }
            }
        };
        this.characterSelectionHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getSource() != null && ((Node) event.getSource()).getId() != null) {
                    ImageView s = (ImageView) event.getSource();
                    getView().handleCharacterInput(GameCharacter.valueOf(s.getId().toUpperCase()));
                }
            }
        };
        this.cardinalPointSelectionHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getSource() != null && ((Node) event.getSource()).getId() != null) {
                    Button s = (Button) event.getSource();
                    getView().handleCardinalPointInput(CardinalPoint.valueOf(s.getId().toUpperCase()));
                }
            }
        };
        this.effectSelectionHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getSource() != null && ((Node) event.getSource()).getId() != null) {
                    Button s = (Button) event.getSource();
                    getView().handleEffectInput(WeaponEffectOrderType.getFromIdentifier(s.getId().toUpperCase()));
                }
            }
        };
        this.decisionSelectionHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getSource() != null && ((Node) event.getSource()).getId() != null) {
                    Button s = (Button) event.getSource();
                    getView().handleDecisionInput(s.getId());
                }
            }
        };
        this.ammosSelectionHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getSource() != null && ((Node) event.getSource()).getId() != null) {
                    AmmoType ammo = AmmoType.valueOf(((Button) event.getSource()).getId());
                    getView().handleAmmoInput(ammo);
                }
            }
        };
    }

    /**
     * Sets the player borad
     */
    @FXML
    void setPlayerBoard() {
        new Thread(() -> getView().setPlayerBoard(getView().getCharacter())).start();
    }

    /**
     * Gets the active character board
     * @return the active game character
     */
    public GameCharacter getActiveBoard() {
        return this.activeBoard;
    }

    /**
     * Open a new stage with the weapon details info
     * @param weapon you want to show info
     */
    void showWeaponInfo(Weapon weapon) {

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML_PATH + "CardDetail.fxml"));
        Parent root1;
        try {
            root1 = loader.load();
        } catch (IOException e) {
            return;
        }
        Stage stage = new Stage();
        stage.setTitle(Character.toUpperCase(weapon.toString().toLowerCase().charAt(0)) +
                weapon.toString().toLowerCase().substring(1));
        stage.setScene(new Scene(root1));
        stage.show();
        CardDetailController controller = loader.getController();
        controller.setWeapon(weapon);
    }

    /**
     * Sets the arena and the corresponding grid for handling objects above it
     */
    public void setArena() {
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

    /**
     * Updates the killshot track, replacing skulls with the killers drops
     */
    public void updateKillshotTrack() {
        Map<Integer, List<GameCharacter>> killshotTrack = getView().getBoard().getKillshotTrack();
        Platform.runLater(() -> {
            int i = 0;
            while (i < 8 - killshotTrack.size()) {
                ImageView img = (ImageView) this.skullsGrid.getChildren().get(i);
                img.setImage(null);
                i++;
            }
            i = 7;
            for (Map.Entry<Integer, List<GameCharacter>> skull : killshotTrack.entrySet()) {
                ImageView img = (ImageView) this.skullsGrid.getChildren().get(i);
                if (skull.getValue().isEmpty()) {
                    img.setImage(new Image(UTILS_PATH + "skull.png"));
                } else if (skull.getValue().size() == 1) {
                    img.setImage(new Image(DROPS_PATH + "drop_" + skull.getValue().get(0).getColor() + ".png"));
                } else if (skull.getValue().size() == 2) {
                    img.setImage(new Image(DROPS_PATH + "drop_" + skull.getValue().get(0).getColor() + "_" +
                            skull.getValue().get(1).getColor() + ".png"));
                }
                if (skull.getValue().size() > 2) {
                    for (int j = 2; j < skull.getValue().size(); j++) {
                        ImageView extraImg = (ImageView) this.extraMarks.getChildren().get(j - 2);
                        extraImg.setImage(new Image(DROPS_PATH + "drop_" + skull.getValue().get(j).getColor() + ".png"));
                    }
                }
                i--;
            }
        });
    }

    /**
     * Sets the player board that has to be shown
     * @param player of which you to show the player board
     */
    public void setPlayerBoard(GameCharacter player) {
        this.activeBoard = player;
        PlayerBoard board;
        String labelText;
        if (player == getView().getCharacter()) {
            board = getView().getSelfPlayerBoard();
            labelText = "Your board";
        } else {
            board = getView().getBoardByCharacter(player);
            labelText = player.toString() + "'s\n" + "board";
        }
        Platform.runLater(() -> {
            if (!board.isFrenzyBoard()) {
                this.damagesGrid.setTranslateX(0);
                this.deathsGrid.setTranslateX(0);
                this.playerBoardImage.setImage(new Image(PLAYER_BOARDS_PATH + "player_board_" +
                        player.toString().toLowerCase() + ".png"));
            } else {
                this.damagesGrid.setTranslateX(10);
                this.deathsGrid.setTranslateX(32);
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
            Platform.runLater(() ->
                    this.pointsVBox.setVisible(false)
            );
        } else {
            Platform.runLater(() ->
                    this.pointsVBox.setVisible(true)
            );
            updatePoints();
        }
        updateWeapons();
        updatePowerups();
    }

    /**
     * Updates marks on a player board
     */
    public void updateBoardMarks() {
        PlayerBoard board;
        if (this.activeBoard == getView().getCharacter()) {
            board = getView().getSelfPlayerBoard();
        } else {
            board = getView().getBoardByCharacter(this.activeBoard);
        }
        Platform.runLater(() -> {
            for (int j = 0; j < 12; j++) {
                ImageView img = (ImageView) this.marksGrid.getChildren().get(j);
                img.setImage(null);
            }
            int i = 0;
            for (GameCharacter c : board.getRevengeMarks()) {
                ImageView img = (ImageView) this.marksGrid.getChildren().get(i);
                img.setImage(new Image(DROPS_PATH + "drop_" + c.getColor() + ".png"));
                i++;
            }
        });
    }

    /**
     * Updates damages on a player board
     */
    public void updateBoardDamages() {
        PlayerBoard board;
        if (this.activeBoard == getView().getCharacter()) {
            board = getView().getSelfPlayerBoard();
        } else {
            board = getView().getBoardByCharacter(this.activeBoard);
        }
        Platform.runLater(() -> {
            for (int j = 0; j < 12; j++) {
                ImageView img = (ImageView) this.damagesGrid.getChildren().get(j);
                img.setImage(null);
            }
            int i = 0;
            for (GameCharacter c : board.getDamages()) {
                ImageView img = (ImageView) this.damagesGrid.getChildren().get(i);
                img.setImage(new Image(DROPS_PATH + "drop_" + c.getColor() + ".png"));
                i++;
            }
        });
    }

    /**
     * Updates killshot points on the player board
     */
    public void updateKillshotPoints() {
        PlayerBoard board;
        if (this.activeBoard == getView().getCharacter()) {
            board = getView().getSelfPlayerBoard();
        } else {
            board = getView().getBoardByCharacter(this.activeBoard);
        }
        Platform.runLater(() -> {
            for (int i = 0; i < KILLSHOT_POINTS_SIZE; i++) {
                ImageView img = (ImageView) this.deathsGrid.getChildren().get(i);
                img.setImage(null);
            }
            for (int i = 0; i < KILLSHOT_POINTS_SIZE - board.getKillshotPoints().size(); i++) {
                ImageView img = (ImageView) this.deathsGrid.getChildren().get(i);
                img.setImage(new Image(UTILS_PATH + "skull.png"));
            }
        });
    }

    /**
     * Updates the available ammo
     */
    public void updateAmmo() {
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
            if (labelText.length()>14) {
                this.ammoLabel.getStyleClass().add("font-ammo-small");
            }
            this.ammoLabel.setText(labelText);
            for (Map.Entry<AmmoType, Integer> ammo : board.getAvailableAmmos().entrySet()) {
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
            }
        });
        this.ammoLabel.getStyleClass().remove("font-ammo-small");
    }

    /**
     * Updates player score raised
     */
    public void updatePoints() {
        SelfPlayerBoard board = getView().getSelfPlayerBoard();
        int score = board.getScore();
        int fourPoints = score/4;
        score = score - fourPoints*4;
        int twoPoints = score/2;
        score = score - twoPoints*2;
        int onePoints = score;
        Platform.runLater(() -> {
            this.scoreLabel.setText("Your Points" + " (" + board.getScore() + ")");
            this.fourPointQty.setText("x" + fourPoints);
            this.twoPointQty.setText("x" + twoPoints);
            this.onePointQty.setText("x" + onePoints);
        });
    }

    /**
     * Updates weapon stores with weapons of their matching colors
     */
    public void updateStores() {
        Platform.runLater(() -> {
            this.storeWeapons = new EnumMap<>(Weapon.class);
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
                    this.storeWeapons.put(w, img);
                        img.setId(w.toString().toLowerCase());
                        img.setImage(new Image(WEAPONS_PATH + w.toString().toLowerCase() + ".png"));
                        img.setOnMousePressed(this.weaponInfoHandler);
                    i++;
                }
                while (i < 3) {
                    ImageView img = (ImageView) grid.getChildren().get(i);
                        img.setId(null);
                        img.setImage(null);
                        img.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.weaponInfoHandler);
                    i++;
                }
            }
        });
    }

    /**
     * Updates player weapons and their status
     */
    public void updateWeapons() {
        Platform.runLater(() -> {
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
                img.setId(w.toString().toLowerCase());
                img.setImage(new Image(WEAPONS_PATH + w.toString().toLowerCase() + ".png"));
                stateImg.setImage(new Image(UTILS_PATH + "unloaded_weapon.png"));
                img.setOnMousePressed(this.weaponInfoHandler);
                i++;
            }
            if (this.activeBoard != getView().getCharacter()) {
                while (i < board.getWeaponsNumber() + 3) {
                    ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
                    ImageView stateImg = (ImageView) this.playerAssetsGrid.getChildren().get(i + 6);
                    img.setId(null);
                    img.setImage(new Image(WEAPONS_PATH + "weapon_back.png"));
                    stateImg.setImage(null);
                    img.setOnMousePressed(this.weaponInfoHandler);
                    i++;
                }
            } else {
                for (Weapon w : getView().getSelfPlayerBoard().getReadyWeapons()) {
                    ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
                    ImageView stateImg = (ImageView) this.playerAssetsGrid.getChildren().get(i + 6);
                    img.setId(w.toString().toLowerCase());
                    img.setImage(new Image(WEAPONS_PATH + w.toString().toLowerCase() + ".png"));
                    stateImg.setImage(new Image(UTILS_PATH + "weapon_ready.png"));
                    img.setOnMousePressed(this.weaponInfoHandler);
                    i++;
                }
            }
            while (i < 6) {
                ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
                ImageView stateImg = (ImageView) this.playerAssetsGrid.getChildren().get(i + 6);
                img.setId(null);
                img.setImage(null);
                stateImg.setImage(null);
                img.setOnMousePressed(this.weaponInfoHandler);
                i++;
            }
        });
    }

    /**
     * Updates player powerups
     */
    public void updatePowerups() {
        Platform.runLater(() -> {
            int i = 6;
            if (this.activeBoard != getView().getCharacter()) {
                PlayerBoard board = getView().getBoardByCharacter(this.activeBoard);
                while (i < board.getPowerupsNumber() + 6) {
                    ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
                    img.setImage(new Image(POWERUPS_PATH + "powerup_back.png"));
                    img.setId(null);
                    i++;
                }
            } else {
                for (Powerup p : getView().getSelfPlayerBoard().getPowerups()) {
                    ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
                    int index = i - 5;
                    img.setImage(new Image(POWERUPS_PATH + p.getType().toString().toLowerCase() + "_" +
                            p.getColor().toString().toLowerCase() + ".png"));
                    img.setId(index + "_" + p.getType().toString().toLowerCase() + "_" +
                            p.getColor().toString().toLowerCase());
                    i++;
                }
            }
            while (i < 9) {
                ImageView img = (ImageView) this.playerAssetsGrid.getChildren().get(i);
                img.setImage(null);
                img.setId(null);
                i++;
            }
        });
    }

    /**
     * Gets the squares grid of the arena matching to the x, y coordinates
     * @param x coordinate of the square of which you want the pane
     * @param y coordinate of the square of which you want the pane
     * @return the matching grid pane
     */
    GridPane getSquarePaneByCoordinates(int x, int y) {
        int arena = getView().getBoard().getArena();
        for (Node n : this.arenaPane.getChildren()) {
            if (n.getId().equalsIgnoreCase("arena" + arena + x + y)) {
                return (GridPane) n;
            }
        }
        return null;
    }

    /**
     * Refills the ammo tiles on the arena
     */
    public void updateTiles() {
        Platform.runLater(() -> {
            for (SquareView s : getView().getBoard().getSquares()) {
                if (s.isSpawn()) {
                    continue;
                }
                ImageView img = (ImageView) getSquarePaneByCoordinates(s.getX(), s.getY())
                        .getChildren().get(getSquarePaneByCoordinates(s.getX(), s.getY()).getChildren().size() - 1);
                if (s.getAvailableAmmoTile() == null) {
                    img.setImage(null);
                } else {
                    img.setImage(new Image(AMMO_TILES_PATH + "ammotile_" + s.getAvailableAmmoTile().getNumber() +
                            ".png"));
                }
            }
        });
    }

    /**
     * Updates a player position on the arena
     */
    public void updatePlayersPositions() {
        Platform.runLater(() -> {
            this.players = new ArrayList<>();
            for (SquareView s : getView().getBoard().getSquares()) {
                GridPane squarePane = getSquarePaneByCoordinates(s.getX(), s.getY());
                for (int j = 0; j < 5; j++) {
                    ImageView img = (ImageView) squarePane.getChildren().get(j);
                    img.setImage(null);
                    img.setId(null);
                    img.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.setPlayerBoardHandler);
                }
                int i = 0;
                for (GameCharacter c : s.getActivePlayers()) {
                    ImageView img = (ImageView) squarePane.getChildren().get(i);
                    this.players.add(img);
                    img.setImage(new Image(CHARACTERS_PATH + c.toString().toLowerCase() + ".png"));
                    img.setId(c.toString().toLowerCase());
                    img.setOnMousePressed(this.setPlayerBoardHandler);
                    PlayerBoard board;
                    if (c == getView().getCharacter()) {
                        board = getView().getSelfPlayerBoard();
                    } else {
                        board = getView().getBoardByCharacter(c);
                    }
                    if (board.isDead()) {
                        img.setRotate(90);
                    } else {
                        img.setRotate(0);
                    }
                    i++;
                }
            }
        });
    }

    /**
     * Shows messages into the console box
     * @param messages List of the messages that have to be shown
     */
    public void showMessage(List<String> messages) {
        String message = messages.stream().collect(Collectors.joining("\n"));
        Platform.runLater(() ->
                this.messagesText.setText(message)
        );
    }

    /**
     * Sets the banner of actions and status
     * @param status of the game
     * @param action that player has to do
     */
    public void setBanner(String status, String action) {
        Platform.runLater(() -> {
            if (status.length() > 37){
                this.currentStatusLabel.getStyleClass().add("font-status-small");
            }
            this.currentStatusLabel.setText(status);
            this.currentActionLabel.setText(action);
        });
        this.currentStatusLabel.getStyleClass().remove("font-status-small");
    }

    /**
     * Sets the available actions for the player, enabling the matching action buttons
     * @param actions List of the type of the actions that can be done
     */
    public void setActions(List<ActionType> actions) {
        Platform.runLater(() -> {
            Button b = (Button) this.actionsPane.getChildren().get(0);
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
            for (Node n : this.actionsPane.getChildren()) {
                if (actions.contains(ActionType.valueOf(n.getId().toUpperCase()))) {
                    n.setDisable(false);
                    n.setOnMousePressed(this.actionSelectionHandler);
                } else {
                    n.setDisable(true);
                    n.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.actionSelectionHandler);
                }
            }
        });
    }

    /**
     * Sets the powerups that can be used and the selected ones
     * @param powerups List of the powerups that can be used
     * @param selected List of the selected ones
     */
    public void setPowerups(List<Powerup> powerups, List<Powerup> selected) {
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
                boolean set = false;
                for (Powerup p : powerups) {
                    if (p.getType() == type && p.getColor() == color) {
                        img.setOnMousePressed(this.powerupSelectionHandler);
                        img.getStyleClass().add(CARD_SELECTABLE_CLASS);
                        img.getStyleClass().remove(CARD_SELECTED_CLASS);
                        set = true;
                        break;
                    }
                }
                if (!set) {
                    for (Powerup p : selected) {
                        if (p.getType() == type && p.getColor() == color) {
                            img.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.powerupSelectionHandler);
                            img.getStyleClass().add(CARD_SELECTED_CLASS);
                            img.getStyleClass().remove(CARD_SELECTABLE_CLASS);
                            set = true;
                            break;
                        }
                    }
                }
                if (!set) {
                    img.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.powerupSelectionHandler);
                    img.getStyleClass().remove(CARD_SELECTED_CLASS);
                    img.getStyleClass().remove(CARD_SELECTABLE_CLASS);
                }
            }
        });
    }

    /**
     * Shows the available squares, which ones you can click on
     * @param coordinates of the squares you want to make clickable
     */
    public void setSquares(List<Coordinates> coordinates) {
        Platform.runLater(() -> {
            if (!coordinates.isEmpty()) {
                this.arenaButtonsContainer.toFront();
            } else {
                this.arenaPane.toFront();
            }
            for (Node s : this.arenaButtonsContainer.getChildren()) {
                boolean valid = false;
                for (Coordinates c : coordinates) {
                    if (s.getId().equalsIgnoreCase("s" + c.getX() + c.getY())) {
                        s.getStyleClass().add("button-square-enable");
                        s.setOnMousePressed(this.squareSelectionHandler);
                        s.setVisible(true);
                        valid = true;
                    }
                }
                if (!valid) {
                    s.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.squareSelectionHandler);
                    s.setVisible(false);
                    s.getStyleClass().remove("button-square-enable");
                }
            }
        });
    }

    /**
     * Sets the weapons clickable for use
     * @param weapons list of the weapons that can be used
     */
    public void setWeapons(List<Weapon> weapons) {
        if (!weapons.isEmpty()) {
            Platform.runLater(() -> {
                for (Map.Entry<Weapon, ImageView> weapon : this.storeWeapons.entrySet()) {
                    Weapon name = weapon.getKey();
                    ImageView img = weapon.getValue();
                    if (weapons.contains(name)) {
                        img.setOnMousePressed(this.weaponSelectionHandler);
                        img.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.weaponInfoHandler);
                        img.getStyleClass().add(CARD_SELECTABLE_CLASS);
                    } else {
                        img.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.weaponInfoHandler);
                        img.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.weaponSelectionHandler);
                        img.getStyleClass().remove(CARD_SELECTABLE_CLASS);
                    }
                }
                for (Node n : this.playerAssetsGrid.getChildren()) {
                    try {
                        if (n.getId() != null && weapons.contains(Weapon.valueOf(n.getId().toUpperCase()))) {
                            n.setOnMousePressed(this.weaponSelectionHandler);
                            n.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.weaponInfoHandler);
                            n.getStyleClass().add(CARD_SELECTABLE_CLASS);
                        } else {
                            n.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.weaponInfoHandler);
                            n.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.weaponSelectionHandler);
                            n.getStyleClass().remove(CARD_SELECTABLE_CLASS);
                        }
                    } catch (IllegalArgumentException e) {
                        n.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.weaponInfoHandler);
                        n.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.weaponSelectionHandler);
                        n.getStyleClass().remove(CARD_SELECTABLE_CLASS);
                    }
                }
            });
        } else {
            Platform.runLater(() -> {
                for (Map.Entry<Weapon, ImageView> weapon : this.storeWeapons.entrySet()) {
                    ImageView img = weapon.getValue();
                    img.setOnMousePressed(this.weaponInfoHandler);
                    img.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.weaponSelectionHandler);
                    img.getStyleClass().remove(CARD_SELECTABLE_CLASS);
                }
                for (Node n : this.playerAssetsGrid.getChildren()) {
                    try {
                        Weapon.valueOf(n.getId().toUpperCase());
                        n.setOnMousePressed(this.weaponInfoHandler);
                        n.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.weaponSelectionHandler);
                        n.getStyleClass().remove(CARD_SELECTABLE_CLASS);
                    } catch (IllegalArgumentException | NullPointerException e) {
                        n.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.weaponInfoHandler);
                        n.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.weaponSelectionHandler);
                        n.getStyleClass().remove(CARD_SELECTABLE_CLASS);
                    }
                }
            });
        }
    }

    /**
     * Matches the secondary buttons to a function and enables them
     * @param buttons List of the messages that the buttons have to contain
     */
    public void setSecondaryButtons(List<String> buttons) {
        Platform.runLater(() -> {
            int i = 0;
            for (String id : buttons) {
                Button b = (Button) this.secondaryButtonsBox.getChildren().get(i);
                EventHandler<MouseEvent> handler;
                String classString;
                if (buttons.get(0).equals("continue")) {
                    handler = this.confirmHandler;
                    classString = BUTTON_CONFIRM_CLASS;
                } else if (WeaponEffectOrderType.getFromIdentifier(buttons.get(0).toUpperCase()) != null) {
                    handler = this.effectSelectionHandler;
                    classString = BUTTON_STD_CLASS;
                } else if (buttons.get(0).equals("y")) {
                    handler = this.decisionSelectionHandler;
                    classString = BUTTON_STD_CLASS;
                } else if (buttons.get(0).equals("red") || buttons.get(0).equals("yellow") ||
                        buttons.get(0).equals("blue")) {
                    handler = this.ammosSelectionHandler;
                    if (id.equals("red")) {
                        classString = "ammo-red";
                    } else if (id.equals("blue")) {
                        classString = "ammo-blue";
                    } else {
                        classString = "ammo-yellow";
                    }
                } else {
                    handler = this.cardinalPointSelectionHandler;
                    classString = BUTTON_STD_CLASS;
                }
                b.setId(id);
                if (id.equals("y")) {
                    b.setText("Yes");
                    b.getStyleClass().add(BUTTON_CONFIRM_CLASS);
                } else if (id.equals("n")) {
                    b.setText("No");
                    b.getStyleClass().add(BUTTON_DANGER_CLASS);
                } else {
                    b.setText(Character.toUpperCase(id.charAt(0)) + id.substring(1));
                }
                b.setVisible(true);
                b.setOnMousePressed(handler);
                b.getStyleClass().add(classString);
                i++;
            }
            while (i < 4) {
                Button b = (Button) this.secondaryButtonsBox.getChildren().get(i);
                b.setId(null);
                b.setVisible(false);
                b.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.confirmHandler);
                b.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.cardinalPointSelectionHandler);
                b.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.effectSelectionHandler);
                b.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.decisionSelectionHandler);
                b.getStyleClass().remove(BUTTON_CONFIRM_CLASS);
                b.getStyleClass().remove(BUTTON_STD_CLASS);
                b.getStyleClass().remove(BUTTON_STD_CLASS);
                b.getStyleClass().remove(BUTTON_DANGER_CLASS);
                b.getStyleClass().remove("ammo-red");
                b.getStyleClass().remove("ammo-blue");
                b.getStyleClass().remove("ammo-yellow");
                i++;
            }
        });
    }

    /**
     * Sets the available targets that can be chosen by player
     * @param targets List of the available characters that can be chosen
     * @param selected List of the selected ones
     */
    public void setTargets(List<GameCharacter> targets, List<GameCharacter> selected) {
        Platform.runLater(() -> {
            if (!targets.isEmpty()) {
                this.arenaPane.toFront();
                for (ImageView player : this.players) {
                    player.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.setPlayerBoardHandler);
                    player.getStyleClass().remove(CHARACTER_SELECTABLE_CLASS);
                    player.getStyleClass().remove(CHARACTER_SELECTED_CLASS);
                    player.getStyleClass().add(CHARACTER_CLASS);
                    if (targets.contains(GameCharacter.valueOf(player.getId().toUpperCase()))) {
                        player.setOnMousePressed(this.characterSelectionHandler);
                        player.getStyleClass().add(CHARACTER_SELECTABLE_CLASS);
                        player.getStyleClass().remove(CHARACTER_CLASS);
                    } else if (selected.contains(GameCharacter.valueOf(player.getId().toUpperCase()))) {
                        player.getStyleClass().add(CHARACTER_SELECTED_CLASS);
                        player.getStyleClass().remove(CHARACTER_CLASS);
                    }
                }
            } else {
                for (ImageView player : this.players) {
                    player.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.characterSelectionHandler);
                    player.setOnMousePressed(this.setPlayerBoardHandler);
                    player.getStyleClass().remove(CHARACTER_SELECTABLE_CLASS);
                    player.getStyleClass().remove(CHARACTER_SELECTED_CLASS);
                    player.getStyleClass().add(CHARACTER_CLASS);
                }
            }
        });
    }
}


