package it.polimi.se2019.view;

import it.polimi.se2019.model.AmmoType;
import it.polimi.se2019.model.Weapon;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Map;

/**
 * Class for handling card detail controller
 */
public class CardDetailController extends AbstractSceneController {

    private static final String AMMO_PATH = "ammos/";
    private static final String UTILS_PATH = "utils/icons/";
    private static final String WEAPONS_PATH = "weapons/img/";

    @FXML
    private ImageView cardImage;
    @FXML
    private HBox costPanel;
    @FXML
    private Label primaryName;
    @FXML
    private Label primaryDescription;
    @FXML
    private Label secondaryOneName;
    @FXML
    private Label secondaryOneDescription;
    @FXML
    private Label secondaryTwoName;
    @FXML
    private Label secondaryTwoDescription;
    @FXML
    private VBox secondaryOnePanel;
    @FXML
    private VBox secondaryTwoPanel;
    @FXML
    private ImageView statusImage;
    @FXML
    private Label statusDescription;

    @FXML
    void close() {
        new Thread(() -> getView().showBoard()).start();
    }

    void setWeapon(Weapon weapon) {
        Platform.runLater(() -> {
            this.cardImage.setImage(new Image(WEAPONS_PATH + weapon.toString().toLowerCase() + ".png"));
            ((ImageView) this.costPanel.getChildren().get(1)).setImage(new Image(AMMO_PATH + "ammo_" +
                    weapon.getColor().toString().toLowerCase() + ".png"));
        });
        int i = 2;
        for (Map.Entry<AmmoType, Integer> ammo : weapon.getBuyCost().entrySet()) {
            for (int j = 0; j < ammo.getValue(); j++) {
                ImageView img = (ImageView) this.costPanel.getChildren().get(i);
                Platform.runLater(() ->
                        img.setImage(new Image(AMMO_PATH + "ammo_" + ammo.getKey().toString().toLowerCase() +
                                ".png"))
                );
                i++;
            }
        }
        Platform.runLater(() -> {
            this.primaryName.setText(weapon.getPrimaryEffect().get(0).getEffectName() + ":");
            this.primaryDescription.setText(weapon.getPrimaryEffect().get(0).getDescription());
        });

        Map<AmmoType, Integer> cost;
        if (!weapon.getAlternativeMode().isEmpty()) {
            cost = weapon.getAlternativeMode().get(0).getCost();
            Platform.runLater(() -> {
                this.secondaryOneName.setText(weapon.getAlternativeMode().get(0).getEffectName() + ":");
                this.secondaryOneDescription.setText(weapon.getAlternativeMode().get(0).getDescription());
            });
        } else {
            cost = weapon.getSecondaryEffectOne().get(0).getCost();
            Platform.runLater(() -> {
                this.secondaryOneName.setText(weapon.getSecondaryEffectOne().get(0).getEffectName() + ":");
                this.secondaryOneDescription.setText(weapon.getSecondaryEffectOne().get(0).getDescription());
            });
        }
        i = 1;
        for (Map.Entry<AmmoType, Integer> ammo : cost.entrySet()) {
            for (int j = 0; j < ammo.getValue(); j++) {
                ImageView img = (ImageView) ((HBox) this.secondaryOnePanel.getChildren().get(0)).getChildren().get(i);
                Platform.runLater(() ->
                        img.setImage(new Image(AMMO_PATH + "ammo_" + ammo.getKey().toString().toLowerCase() +
                                ".png"))
                );
                i++;
            }
        }
        if (!weapon.getSecondaryEffectTwo().isEmpty()) {
            Platform.runLater(() -> {
                this.secondaryTwoName.setText(weapon.getSecondaryEffectTwo().get(0).getEffectName() + ":");
                this.secondaryTwoDescription.setText(weapon.getSecondaryEffectTwo().get(0).getDescription());
            });
            i = 1;
            for (Map.Entry<AmmoType, Integer> ammo : weapon.getSecondaryEffectTwo().get(0).getCost().entrySet()) {
                for (int j = 0; j < ammo.getValue(); j++) {
                    ImageView img = (ImageView) ((HBox) this.secondaryTwoPanel.getChildren().get(0)).getChildren().get(i);
                    Platform.runLater(() ->
                            img.setImage(new Image(AMMO_PATH + "ammo_" + ammo.getKey().toString().toLowerCase() +
                                    ".png"))
                    );
                    i++;
                }
            }
        }

        if (isReady(weapon)) {
            Platform.runLater(() -> {
                this.statusImage.setImage(new Image(UTILS_PATH + "weapon_ready.png"));
                this.statusDescription.setText("ready");
            });
        } else {
            Platform.runLater(() -> {
                this.statusImage.setImage(new Image(UTILS_PATH + "unloaded_weapon.png"));
                this.statusDescription.setText("unload");
            });
        }
    }

    private boolean isReady(Weapon weapon) {
        for (SquareView s : getView().getBoard().getSquares()) {
            if (!s.isSpawn()) {
                continue;
            }
            if (s.getStore().contains(weapon)) {
                return true;
            }
        }
        return getView().getSelfPlayerBoard().getReadyWeapons().contains(weapon);
    }
}
