package dao;

import model.Pet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PetDAO {

    public Pet getFirstPet() throws SQLException {
        String sql = "SELECT id, nome, tipo_usuario, hunger, happiness, energy, image_data "
                   + "FROM pet ORDER BY id LIMIT 1";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return map(rs, true);
        }
        return null;
    }

    public Pet getActivePet() throws SQLException {
        String sql = "SELECT p.id, p.nome, p.tipo_usuario, p.hunger, p.happiness, p.energy, p.image_data "
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
                   + "happiness = ?, energy = ?, image_data = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pet.getNome());
            ps.setString(2, pet.getTipoUsuario());
            ps.setInt(3, pet.getHunger());
            ps.setInt(4, pet.getHappiness());
            ps.setInt(5, pet.getEnergy());
            ps.setBytes(6, pet.getImageData());
            ps.setInt(7, pet.getId());
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Pet nao encontrado para atualizar: id=" + pet.getId());
            }
        }
    }

    public void insert(Pet pet) throws SQLException {
        normalizePet(pet);
        String sql = "INSERT INTO pet (nome, tipo_usuario, hunger, happiness, energy, image_data) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pet.getNome());
            ps.setString(2, pet.getTipoUsuario());
            ps.setInt(3, pet.getHunger());
            ps.setInt(4, pet.getHappiness());
            ps.setInt(5, pet.getEnergy());
            ps.setBytes(6, pet.getImageData());
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
        String sql = "SELECT id, nome, tipo_usuario, hunger, happiness, energy "
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
        String sql = "SELECT id, nome, tipo_usuario, hunger, happiness, energy, image_data "
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
        if (includeImage) {
            p.setImageData(rs.getBytes("image_data"));
        }
        return p;
    }
}
