package controller;

import dao.CreatureDAO;
import dao.PetDAO;
import model.Pet;

import java.sql.SQLException;
import java.util.List;

public class GameController {

    private final PetDAO petDAO = new PetDAO();
    private final CreatureDAO creatureDAO = new CreatureDAO();

    private static final long SECONDS_PER_MINUTE = 60L;
    private static final int HUNGER_DECAY_MINUTES = 5;
    private static final int HAPPINESS_DECAY_MINUTES = 10;
    private static final int ENERGY_DECAY_MINUTES = 8;

    private Pet petAtual;

    public void carregarPet() throws SQLException {
        petAtual = petDAO.getActivePet();
        atualizarNecessidadesPorTempo();
    }

    public void carregarPetPorId(int id) throws SQLException {
        petAtual = petDAO.buscarPorId(id);
        if (petAtual != null) {
            petDAO.setActivePet(id);
            atualizarNecessidadesPorTempo();
        }
    }

    public Pet getPetAtual() {
        return petAtual;
    }

    public void alimentar() throws SQLException {
        if (petAtual == null) return;
        atualizarNecessidadesPorTempo();
        alterarStatus(+20, 0, 0);
        registrarCuidado("alimentar", "Fome +20");
        checkEvolution();
    }

    public void brincar() throws SQLException {
        if (petAtual == null) return;
        atualizarNecessidadesPorTempo();
        int ganhoFelicidade = petAtual.getEnergy() < 15 ? 4 : petAtual.getEnergy() < 40 ? 9 : 15;
        if (petAtual.getHappiness() < 15) {
            ganhoFelicidade = Math.max(4, ganhoFelicidade - 5);
        }
        alterarStatus(-4, ganhoFelicidade, -5);
        registrarCuidado("brincar", "Fome -4, felicidade +" + ganhoFelicidade + ", energia -5");
        checkEvolution();
    }

    public void dormir() throws SQLException {
        if (petAtual == null) return;
        atualizarNecessidadesPorTempo();
        alterarStatus(-3, +2, +30);
        registrarCuidado("dormir", "Fome -3, felicidade +2, energia +30");
        checkEvolution();
    }

    public void exercitar() throws SQLException {
        if (petAtual == null) return;
        atualizarNecessidadesPorTempo();
        if (petAtual.getEnergy() <= 14) {
            registrarCuidado("exercitar_bloqueado", "Energia insuficiente para exercitar");
            return;
        }
        int ganhoFelicidade = petAtual.getEnergy() < 40 ? 5 : 10;
        alterarStatus(-8, ganhoFelicidade, -10);
        registrarCuidado("exercitar", "Fome -8, felicidade +" + ganhoFelicidade + ", energia -10");
        checkEvolution();
    }

    public void atualizarNecessidadesPorTempo() throws SQLException {
        if (petAtual == null) return;

        long now = nowEpoch();
        long last = petAtual.getLastNeedsUpdateEpoch();
        if (last <= 0 || last > now) {
            petAtual.setLastNeedsUpdateEpoch(now);
            petDAO.update(petAtual);
            return;
        }

        long elapsedMinutes = (now - last) / SECONDS_PER_MINUTE;
        if (elapsedMinutes <= 0) {
            return;
        }

        int hungerLoss = (int) (elapsedMinutes / HUNGER_DECAY_MINUTES);
        int happinessLoss = (int) (elapsedMinutes / HAPPINESS_DECAY_MINUTES);
        int energyLoss = (int) (elapsedMinutes / ENERGY_DECAY_MINUTES);
        int penaltyCycles = (int) (elapsedMinutes / HUNGER_DECAY_MINUTES);

        int projectedHunger = clamp(petAtual.getHunger() - hungerLoss);
        int projectedEnergy = clamp(petAtual.getEnergy() - energyLoss);

        if (projectedHunger < 20 && penaltyCycles > 0) {
            happinessLoss += penaltyCycles * 2;
            energyLoss += penaltyCycles;
        }
        if (projectedEnergy < 20 && penaltyCycles > 0) {
            happinessLoss += penaltyCycles * 2;
        }

        petAtual.setHunger(clamp(petAtual.getHunger() - hungerLoss));
        petAtual.setHappiness(clamp(petAtual.getHappiness() - happinessLoss));
        petAtual.setEnergy(clamp(petAtual.getEnergy() - energyLoss));
        petAtual.setLastNeedsUpdateEpoch(now);

        int average = mediaGeral();
        if (average >= 70) {
            petAtual.setHealthyMinutes(safeAdd(petAtual.getHealthyMinutes(), (int) elapsedMinutes));
        }
        if (average < 30 || petAtual.getHunger() < 20 || petAtual.getEnergy() < 20) {
            petAtual.setNeglectMinutes(safeAdd(petAtual.getNeglectMinutes(), (int) elapsedMinutes));
        }

        petDAO.update(petAtual);
        if (hungerLoss > 0 || happinessLoss > 0 || energyLoss > 0) {
            petDAO.recordCareHistory(petAtual, "tempo", "Decaimento por " + elapsedMinutes + " min");
        }
    }

    private void checkEvolution() throws SQLException {
        if (petAtual == null) return;
        List<CreatureDAO.EvolutionRule> rules =
                creatureDAO.getEvolutionsForNome(petAtual.getTipoUsuario());
        for (CreatureDAO.EvolutionRule rule : rules) {
            int minFood = 100 - rule.maxHunger;
            if (mediaGeral() >= 50
                    && petAtual.getHunger() >= minFood
                    && petAtual.getHappiness() >= rule.minHappiness) {
                String novoTipo = creatureDAO.getNomeById(rule.toCreatureId);
                if (novoTipo != null) {
                    petAtual.setTipoUsuario(novoTipo);
                    petDAO.update(petAtual);
                    petDAO.recordCareHistory(petAtual, "evolucao", "Evoluiu para " + novoTipo);
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

    private void registrarCuidado(String tipo, String nota) throws SQLException {
        petAtual.setCareCount(safeAdd(petAtual.getCareCount(), 1));
        petAtual.setLastNeedsUpdateEpoch(nowEpoch());
        petDAO.update(petAtual);
        petDAO.recordCareHistory(petAtual, tipo, nota);
    }

    private int mediaGeral() {
        if (petAtual == null) return 0;
        return (petAtual.getHunger() + petAtual.getHappiness() + petAtual.getEnergy()) / 3;
    }

    private int safeAdd(int current, int delta) {
        long sum = (long) current + delta;
        return sum > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) sum;
    }

    private long nowEpoch() {
        return System.currentTimeMillis() / 1000L;
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(100, v));
    }
}
