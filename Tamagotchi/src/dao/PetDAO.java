package dao;

import model.Pet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PetDAO {

    public Pet getFirstPet() throws SQLException {
        String sql = selectColumns(true)
                   + "FROM pet ORDER BY id LIMIT 1";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return map(rs, true);
        }
        return null;
    }

    public Pet getActivePet() throws SQLException {
        String sql = "SELECT p.id, p.nome, p.tipo_usuario, p.hunger, p.happiness, p.energy, "
                   + "p.last_needs_update_epoch, p.healthy_minutes, p.care_count, "
                   + "p.neglect_minutes, p.image_data "
                   + "FROM pet p "
                   + "JOIN app_setting s ON s.chave = 'active_pet_id' "
                   + "AND CAST(s.valor AS INTEGER) = p.id "
                   + "LIMIT 1";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return map(rs, true);
        }
        Pet first = getFirstPet();
        if (first != null) {
            setActivePet(first.getId());
        }
        return first;
    }

    public void setActivePet(int id) throws SQLException {
        String sql = "INSERT INTO app_setting (chave, valor) VALUES ('active_pet_id', ?) "
                   + "ON CONFLICT(chave) DO UPDATE SET valor = excluded.valor";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Integer.toString(id));
            ps.executeUpdate();
        }
    }

    public void update(Pet pet) throws SQLException {
        normalizePet(pet);
        String sql = "UPDATE pet SET nome = ?, tipo_usuario = ?, hunger = ?, "
                   + "happiness = ?, energy = ?, last_needs_update_epoch = ?, "
                   + "healthy_minutes = ?, care_count = ?, neglect_minutes = ?, "
                   + "image_data = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pet.getNome());
            ps.setString(2, pet.getTipoUsuario());
            ps.setInt(3, pet.getHunger());
            ps.setInt(4, pet.getHappiness());
            ps.setInt(5, pet.getEnergy());
            ps.setLong(6, pet.getLastNeedsUpdateEpoch());
            ps.setInt(7, pet.getHealthyMinutes());
            ps.setInt(8, pet.getCareCount());
            ps.setInt(9, pet.getNeglectMinutes());
            ps.setBytes(10, pet.getImageData());
            ps.setInt(11, pet.getId());
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Pet nao encontrado para atualizar: id=" + pet.getId());
            }
        }
    }

    public void insert(Pet pet) throws SQLException {
        normalizePet(pet);
        String sql = "INSERT INTO pet (nome, tipo_usuario, hunger, happiness, energy, "
                   + "last_needs_update_epoch, healthy_minutes, care_count, neglect_minutes, image_data) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pet.getNome());
            ps.setString(2, pet.getTipoUsuario());
            ps.setInt(3, pet.getHunger());
            ps.setInt(4, pet.getHappiness());
            ps.setInt(5, pet.getEnergy());
            ps.setLong(6, pet.getLastNeedsUpdateEpoch());
            ps.setInt(7, pet.getHealthyMinutes());
            ps.setInt(8, pet.getCareCount());
            ps.setInt(9, pet.getNeglectMinutes());
            ps.setBytes(10, pet.getImageData());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) pet.setId(rs.getInt(1));
            }
            if (pet.getId() > 0 && getActivePet() == null) {
                setActivePet(pet.getId());
            }
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM pet WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Pet nao encontrado para excluir: id=" + id);
            }
        }

        Pet active = getActivePet();
        if (active == null || active.getId() == id) {
            Pet first = getFirstPet();
            if (first != null) {
                setActivePet(first.getId());
            } else {
                clearActivePet();
            }
        }
    }

    public List<Pet> listAll() throws SQLException {
        String sql = "SELECT id, nome, tipo_usuario, hunger, happiness, energy, "
                   + "last_needs_update_epoch, healthy_minutes, care_count, neglect_minutes "
                   + "FROM pet ORDER BY id";
        List<Pet> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs, false));
        }
        return list;
    }

    public Pet buscarPorId(int id) throws SQLException {
        String sql = selectColumns(true)
                   + "FROM pet WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs, true);
            }
        }
        return null;
    }

    private void clearActivePet() throws SQLException {
        String sql = "DELETE FROM app_setting WHERE chave = 'active_pet_id'";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    public void recordCareHistory(Pet pet, String eventType, String note) throws SQLException {
        if (pet == null || pet.getId() <= 0) {
            return;
        }
        String sql = "INSERT INTO pet_care_history "
                   + "(pet_id, event_type, event_epoch, hunger, happiness, energy, average, note) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pet.getId());
            ps.setString(2, eventType);
            ps.setLong(3, System.currentTimeMillis() / 1000L);
            ps.setInt(4, pet.getHunger());
            ps.setInt(5, pet.getHappiness());
            ps.setInt(6, pet.getEnergy());
            ps.setInt(7, (pet.getHunger() + pet.getHappiness() + pet.getEnergy()) / 3);
            ps.setString(8, note);
            ps.executeUpdate();
        }
    }

    private void normalizePet(Pet pet) throws SQLException {
        if (pet == null) {
            throw new SQLException("Pet nao pode ser nulo.");
        }

        String nome = pet.getNome() != null ? pet.getNome().trim() : "";
        String tipo = pet.getTipoUsuario() != null ? pet.getTipoUsuario().trim() : "";
        if (nome.isEmpty()) {
            throw new SQLException("Nome do pet e obrigatorio.");
        }
        if (tipo.isEmpty()) {
            throw new SQLException("Tipo do pet e obrigatorio.");
        }

        String canonicalTipo = new CreatureDAO().getNomeCanonical(tipo);
        pet.setNome(nome);
        pet.setTipoUsuario(canonicalTipo != null ? canonicalTipo : tipo);
        pet.setHunger(clamp(pet.getHunger()));
        pet.setHappiness(clamp(pet.getHappiness()));
        pet.setEnergy(clamp(pet.getEnergy()));
        if (pet.getLastNeedsUpdateEpoch() <= 0) {
            pet.setLastNeedsUpdateEpoch(System.currentTimeMillis() / 1000L);
        }
        pet.setHealthyMinutes(Math.max(0, pet.getHealthyMinutes()));
        pet.setCareCount(Math.max(0, pet.getCareCount()));
        pet.setNeglectMinutes(Math.max(0, pet.getNeglectMinutes()));
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    private Pet map(ResultSet rs, boolean includeImage) throws SQLException {
        Pet p = new Pet();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setTipoUsuario(rs.getString("tipo_usuario"));
        p.setHunger(rs.getInt("hunger"));
        p.setHappiness(rs.getInt("happiness"));
        p.setEnergy(rs.getInt("energy"));
        p.setLastNeedsUpdateEpoch(rs.getLong("last_needs_update_epoch"));
        p.setHealthyMinutes(rs.getInt("healthy_minutes"));
        p.setCareCount(rs.getInt("care_count"));
        p.setNeglectMinutes(rs.getInt("neglect_minutes"));
        if (includeImage) {
            p.setImageData(rs.getBytes("image_data"));
        }
        return p;
    }

    private String selectColumns(boolean includeImage) {
        String columns = "SELECT id, nome, tipo_usuario, hunger, happiness, energy, "
                + "last_needs_update_epoch, healthy_minutes, care_count, neglect_minutes";
        if (includeImage) {
            columns += ", image_data ";
        } else {
            columns += " ";
        }
        return columns;
    }
}
