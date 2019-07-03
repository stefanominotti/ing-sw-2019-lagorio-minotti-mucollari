package it.polimi.se2019.view.guicontrollers;

import it.polimi.se2019.model.playerassets.AmmoType;
import it.polimi.se2019.model.playerassets.weapons.Weapon;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.Map;

/**
 * Class for handling card detail controller
 */
public class CardDetailController extends AbstractSceneController {

    private static final String AMMO_PATH = "ammos/";
    private static final String WEAPONS_PATH = "weapons/img/";

    @FXML
    private ImageView cardImage;
    @FXML
    private HBox costPanel;
    @FXML
    private Label primaryName;
    @FXML
    private Text primaryDescription;
    @FXML
    private Label secondaryOneName;
    @FXML
    private Text secondaryOneDescription;
    @FXML
    private Label secondaryTwoName;
    @FXML
    private Text secondaryTwoDescription;
    @FXML
    private VBox secondaryOnePanel;
    @FXML
    private VBox secondaryTwoPanel;

    @FXML
    void close() {
        Stage stage = (Stage) cardImage.getScene().getWindow();
        stage.close();
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

        Map<AmmoType, Integer> cost = new EnumMap<>(AmmoType.class);
        if (!weapon.getAlternativeMode().isEmpty()) {
            cost = weapon.getAlternativeMode().get(0).getCost();
            Platform.runLater(() -> {
                this.secondaryOneName.setText(weapon.getAlternativeMode().get(0).getEffectName() + ":");
                this.secondaryOneDescription.setText(weapon.getAlternativeMode().get(0).getDescription());
            });
        } else if (!weapon.getSecondaryEffectOne().isEmpty()) {
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
    }
}
