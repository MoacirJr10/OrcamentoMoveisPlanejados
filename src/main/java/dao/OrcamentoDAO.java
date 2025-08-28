package dao;

import model.Cliente;
import model.Item;
import model.Orcamento;
import model.OrcamentoItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrcamentoDAO {
    public void create(Orcamento orcamento) throws SQLException {
        String sqlOrc = "INSERT INTO orcamentos (cliente_id, data, total) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtOrc = conn.prepareStatement(sqlOrc, Statement.RETURN_GENERATED_KEYS)) {
                stmtOrc.setInt(1, orcamento.getCliente().getId());
                stmtOrc.setDate(2, new java.sql.Date(orcamento.getData().getTime()));
                stmtOrc.setDouble(3, orcamento.getTotal());
                stmtOrc.executeUpdate();
                ResultSet rs = stmtOrc.getGeneratedKeys();
                if (rs.next()) orcamento.setId(rs.getInt(1));

                String sqlItem = "INSERT INTO orcamento_itens (orcamento_id, item_id, quantidade) VALUES (?, ?, ?)";
                try (PreparedStatement stmtItem = conn.prepareStatement(sqlItem)) {
                    for (OrcamentoItem oi : orcamento.getItens()) {
                        stmtItem.setInt(1, orcamento.getId());
                        stmtItem.setInt(2, oi.getItem().getId());
                        stmtItem.setInt(3, oi.getQuantidade());
                        stmtItem.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Orcamento> readAll() throws SQLException {
        String sql = "SELECT o.id, o.data, o.total, c.id as cliente_id, c.nome, c.telefone, c.endereco " +
                "FROM orcamentos o JOIN clientes c ON o.cliente_id = c.id";
        List<Orcamento> orcamentos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Cliente c = new Cliente(rs.getString("nome"), rs.getString("telefone"), rs.getString("endereco"));
                c.setId(rs.getInt("cliente_id"));
                Orcamento o = new Orcamento(c, rs.getDate("data"));
                o.setId(rs.getInt("id"));
                o.setTotal(rs.getDouble("total"));
                // Carregar itens
                o.getItens().addAll(carregarItensOrcamento(conn, o.getId()));
                orcamentos.add(o);
            }
        }
        return orcamentos;
    }

    public List<OrcamentoItem> carregarItensOrcamento(Connection conn, int orcamentoId) throws SQLException {
        String sql = "SELECT i.id, i.descricao, i.preco_unitario, i.dimensao, oi.quantidade " +
                "FROM orcamento_itens oi JOIN itens i ON oi.item_id = i.id WHERE oi.orcamento_id = ?";
        List<OrcamentoItem> itens = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orcamentoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Item i = new Item(rs.getString("descricao"), rs.getDouble("preco_unitario"), rs.getString("dimensao"));
                    i.setId(rs.getInt("id"));
                    itens.add(new OrcamentoItem(i, rs.getInt("quantidade")));
                }
            }
        }
        return itens;
    }
}