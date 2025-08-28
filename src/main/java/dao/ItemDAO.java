package dao;

import model.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    public void create(Item item) throws SQLException {
        String sql = "INSERT INTO itens (descricao, preco_unitario, dimensao) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, item.getDescricao());
            stmt.setDouble(2, item.getPrecoUnitario());
            stmt.setString(3, item.getDimensao());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) item.setId(rs.getInt(1));
        }
    }

    public void update(Item item) throws SQLException {
        String sql = "UPDATE itens SET descricao = ?, preco_unitario = ?, dimensao = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getDescricao());
            stmt.setDouble(2, item.getPrecoUnitario());
            stmt.setString(3, item.getDimensao());
            stmt.setInt(4, item.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM itens WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Item> readAll() throws SQLException {
        String sql = "SELECT * FROM itens";
        List<Item> itens = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Item i = new Item();
                i.setId(rs.getInt("id"));
                i.setDescricao(rs.getString("descricao"));
                i.setPrecoUnitario(rs.getDouble("preco_unitario"));
                i.setDimensao(rs.getString("dimensao"));
                itens.add(i);
            }
        }
        return itens;
    }
}