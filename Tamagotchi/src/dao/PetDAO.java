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
            if (rs.next()) return map(rs);
        }
        return null;
    }

    public void update(Pet pet) throws SQLException {
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
            ps.executeUpdate();
        }
    }

    public void insert(Pet pet) throws SQLException {
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
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM pet WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Pet> listAll() throws SQLException {
        String sql = "SELECT id, nome, tipo_usuario, hunger, happiness, energy, image_data "
                   + "FROM pet ORDER BY id";
        List<Pet> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
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
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    private Pet map(ResultSet rs) throws SQLException {
        Pet p = new Pet();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setTipoUsuario(rs.getString("tipo_usuario"));
        p.setHunger(rs.getInt("hunger"));
        p.setHappiness(rs.getInt("happiness"));
        p.setEnergy(rs.getInt("energy"));
        p.setImageData(rs.getBytes("image_data"));
        return p;
    }
}
