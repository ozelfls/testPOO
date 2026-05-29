package model;

public class Pet {

    private int id;
    private String nome;
    private String tipoUsuario; 
    private int hunger;
    private int happiness;
    private int energy;
    private long lastNeedsUpdateEpoch;
    private int healthyMinutes;
    private int careCount;
    private int neglectMinutes;
    private byte[] imageData;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public int getHappiness() {
        return happiness;
    }

    public void setHappiness(int happiness) {
        this.happiness = happiness;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public long getLastNeedsUpdateEpoch() {
        return lastNeedsUpdateEpoch;
    }

    public void setLastNeedsUpdateEpoch(long lastNeedsUpdateEpoch) {
        this.lastNeedsUpdateEpoch = lastNeedsUpdateEpoch;
    }

    public int getHealthyMinutes() {
        return healthyMinutes;
    }

    public void setHealthyMinutes(int healthyMinutes) {
        this.healthyMinutes = healthyMinutes;
    }

    public int getCareCount() {
        return careCount;
    }

    public void setCareCount(int careCount) {
        this.careCount = careCount;
    }

    public int getNeglectMinutes() {
        return neglectMinutes;
    }

    public void setNeglectMinutes(int neglectMinutes) {
        this.neglectMinutes = neglectMinutes;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
}

