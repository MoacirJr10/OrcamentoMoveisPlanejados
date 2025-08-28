package dao;

import model.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    public void create(Item item) throws SQLException {
        String sql = "INSERT INTO itens (descricao, preco_unitario, dimensao) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, item.getDescricao());
            stmt.setDouble(2, item.getPrecoUnitario());
            stmt.setString(3, item.getDimensao());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<Item> readAll() throws SQLException {
        List<Item> itens = new ArrayList<>();
        String sql = "SELECT * FROM itens";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Item i = new Item(
                        rs.getString("descricao"),
                        rs.getDouble("preco_unitario"),
                        rs.getString("dimensao")
                );
                i.setId(rs.getInt("id"));
                itens.add(i);
            }
        }
        return itens;
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
}