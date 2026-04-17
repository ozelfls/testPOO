package model;

public class CreatureType {

    private int id;
    private String nome;
    private int estagio;

    public CreatureType() {
    }

    public CreatureType(int id, String nome, int estagio) {
        this.id = id;
        this.nome = nome;
        this.estagio = estagio;
    }

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

    public int getEstagio() {
        return estagio;
    }

    public void setEstagio(int estagio) {
        this.estagio = estagio;
    }

    @Override
    public String toString() {
        return nome;
    }
}

