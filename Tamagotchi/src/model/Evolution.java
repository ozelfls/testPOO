package model;

public class Evolution {

    private int id;
    private int fromCreatureId;
    private int toCreatureId;
    private int minHappiness;
    private int maxHunger;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromCreatureId() {
        return fromCreatureId;
    }

    public void setFromCreatureId(int fromCreatureId) {
        this.fromCreatureId = fromCreatureId;
    }

    public int getToCreatureId() {
        return toCreatureId;
    }

    public void setToCreatureId(int toCreatureId) {
        this.toCreatureId = toCreatureId;
    }

    public int getMinHappiness() {
        return minHappiness;
    }

    public void setMinHappiness(int minHappiness) {
        this.minHappiness = minHappiness;
    }

    public int getMaxHunger() {
        return maxHunger;
    }

    public void setMaxHunger(int maxHunger) {
        this.maxHunger = maxHunger;
    }
}

