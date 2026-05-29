package controller;

import dao.CreatureDAO;
import dao.PetDAO;
import model.Pet;

import java.sql.SQLException;
import java.util.List;

public class GameController {

    private final PetDAO petDAO = new PetDAO();
    private final CreatureDAO creatureDAO = new CreatureDAO();

    private Pet petAtual;

    public void carregarPet() throws SQLException {
        petAtual = petDAO.getActivePet();
    }

    public void carregarPetPorId(int id) throws SQLException {
        petAtual = petDAO.buscarPorId(id);
        if (petAtual != null) {
            petDAO.setActivePet(id);
        }
    }

    public Pet getPetAtual() {
        return petAtual;
    }

    public void alimentar() throws SQLException {
        alterarStatus(-20, +5, -5);
        checkEvolution();
    }

    public void brincar() throws SQLException {
        alterarStatus(+12, +20, -18);
        checkEvolution();
    }

    public void dormir() throws SQLException {
        // Dormir recupera muita energia, gera fome gradual e melhora levemente humor
        alterarStatus(+10, +8, +30);
        checkEvolution();
    }

    public void exercitar() throws SQLException {
        alterarStatus(+15, +10, -25);
        checkEvolution();
    }

    private void checkEvolution() throws SQLException {
        if (petAtual == null) return;
        List<CreatureDAO.EvolutionRule> rules =
                creatureDAO.getEvolutionsForNome(petAtual.getTipoUsuario());
        for (CreatureDAO.EvolutionRule rule : rules) {
            if (petAtual.getHappiness() >= rule.minHappiness
                    && petAtual.getHunger() <= rule.maxHunger) {
                String novoTipo = creatureDAO.getNomeById(rule.toCreatureId);
                if (novoTipo != null) {
                    petAtual.setTipoUsuario(novoTipo);
                    petDAO.update(petAtual);
                }
                break;
            }
        }
    }

    private void alterarStatus(int deltaHunger, int deltaHappiness, int deltaEnergy)
            throws SQLException {
        if (petAtual == null) return;
        petAtual.setHunger(clamp(petAtual.getHunger() + deltaHunger));
        petAtual.setHappiness(clamp(petAtual.getHappiness() + deltaHappiness));
        petAtual.setEnergy(clamp(petAtual.getEnergy() + deltaEnergy));
        petDAO.update(petAtual);
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(100, v));
    }
}
