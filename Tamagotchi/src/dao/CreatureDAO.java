package dao;

import model.CreatureType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CreatureDAO {

    public CreatureType getById(int id) throws SQLException {
        String sql = "SELECT id, nome, estagio FROM creature_type WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public String getNomeById(int id) throws SQLException {
        String sql = "SELECT nome FROM creature_type WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("nome");
            }
        }
        return null;
    }

    public List<CreatureType> listAll() throws SQLException {
        String sql = "SELECT id, nome, estagio FROM creature_type ORDER BY id";
        List<CreatureType> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<EvolutionRule> getEvolutionsFor(int fromCreatureId) throws SQLException {
        String sql = "SELECT id, from_creature, to_creature, min_happiness, max_hunger "
                   + "FROM evolution WHERE from_creature = ?";
        List<EvolutionRule> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fromCreatureId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRule(rs));
            }
        }
        return list;
    }

    /** Busca regras de evolução pelo nome atual do tipo (ex: "Baby"). */
    public List<EvolutionRule> getEvolutionsForNome(String nomeAtual) throws SQLException {
        if (nomeAtual == null || nomeAtual.isBlank()) return new ArrayList<>();
        String sql = "SELECT e.id, e.from_creature, e.to_creature, e.min_happiness, e.max_hunger "
                   + "FROM evolution e "
                   + "JOIN creature_type c ON c.id = e.from_creature "
                   + "WHERE c.nome = ?";
        List<EvolutionRule> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomeAtual);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRule(rs));
            }
        }
        return list;
    }

    private CreatureType map(ResultSet rs) throws SQLException {
        return new CreatureType(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getInt("estagio")
        );
    }

    private EvolutionRule mapRule(ResultSet rs) throws SQLException {
        EvolutionRule er = new EvolutionRule();
        er.id = rs.getInt("id");
        er.fromCreatureId = rs.getInt("from_creature");
        er.toCreatureId = rs.getInt("to_creature");
        er.minHappiness = rs.getInt("min_happiness");
        er.maxHunger = rs.getInt("max_hunger");
        return er;
    }

    public static class EvolutionRule {
        public int id;
        public int fromCreatureId;
        public int toCreatureId;
        public int minHappiness;
        public int maxHunger;
    }
}
