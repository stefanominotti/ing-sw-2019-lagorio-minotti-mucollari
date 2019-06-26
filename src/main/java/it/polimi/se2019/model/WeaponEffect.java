package it.polimi.se2019.model;

import java.util.*;

/**
 * Class for handling weapon effects
 */
public class WeaponEffect {

    private final String effectName;
    private final String description;
    private final Map<AmmoType, Integer> cost;
    private boolean effectRequired;
    private boolean effectCombo;
    private final List<WeaponEffectOrderType> effectDependency;
    private final EffectType type;
    private final List<String> amount;
    private final EffectTarget target;
    private final int requiredDependency;

    /**
     * Class constructor, it builds a weapon effect
     * @param effectName name of the effect
     * @param description description of the effect
     * @param cost Map with ammo and its quantity as cost of the effect
     * @param effectRequired true if it is mandatory, else false
     * @param effectCombo true if it is a combo with other effects, else false
     * @param effectDependency list of the effect dependencies from other effects
     * @param type type of the effect
     * @param amount amount of the effect to apply(eg. amount of damages, moves, marks)
     * @param target target of the effect
     * @param requiredDependency index of the mandatory effect in the effect dependencies list
     */
    WeaponEffect(String effectName, String description, Map<AmmoType, Integer> cost, boolean effectRequired,
                 boolean effectCombo, List<WeaponEffectOrderType> effectDependency, EffectType type, List<String> amount,
                 EffectTarget target, int requiredDependency) {

        this.effectName = effectName;
        this.description = description;
        this.cost = new EnumMap<>(AmmoType.class);
        this.cost.putAll(cost);
        this.effectRequired = effectRequired;
        this.effectCombo = effectCombo;
        this.effectDependency = effectDependency;
        this.type = type;
        this.amount = amount;
        this.target = target;
        this.requiredDependency = requiredDependency;
    }

    /**
     * Gets the effect name
     * @return the name of the effect
     */
    public String getEffectName() { return this.effectName; }

    /**
     * Gets the effect description
     * @return the description of the effect
     */
    public String getDescription() { return this.description; }

    /**
     * Gets the effect dependencies from other effects
     * @return List of the dependencies, empty if it has none
     */
    public List<WeaponEffectOrderType> getEffectDependency() {
        if(this.effectDependency == null){
            return new ArrayList<>();
        }
        return new ArrayList<>(this.effectDependency);
    }

    /**
     * Gets the effect type
     * @return the type of the effect
     */
    public EffectType getType() {
        return this.type;
    }

    /**
     * Gets the effect amount to apply
     * @return List of the amounts to apply
     */
    public List<String> getAmount() {
        if(this.amount == null){
            return new ArrayList<>();
        }
        return new ArrayList<>(this.amount);
    }

    /**
     * Gets the effect target
     * @return the target of the effect
     */
    public EffectTarget getTarget() {
        return this.target;
    }

    /**
     * Knows if the effect is mandatory
     * @return true if it is, else false
     */
    public boolean isRequired() { return this.effectRequired; }

    /**
     * Knows if the effect can be a combo
     * @return true if it is, else false
     */
    public boolean isCombo() { return this.effectCombo; }

    /**
     * Gets the effect cost
     * @return Map with ammo and its quantity
     */
    public Map<AmmoType, Integer> getCost() {
        Map<AmmoType, Integer> returnMap = new EnumMap<>(AmmoType.class);
        returnMap.putAll(this.cost);
        return returnMap;
    }

    /**
     * Gets the mandatory dependency of the effect
     * @return Integer with the mandatory effect dependency
     */
    public Integer getRequiredDependency() {
        return this.requiredDependency;
    }
}
