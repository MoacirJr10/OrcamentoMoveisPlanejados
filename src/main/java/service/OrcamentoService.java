package service;

import dao.DatabaseConnection;
import dao.OrcamentoDAO;
import model.Orcamento;
import model.OrcamentoItem;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrcamentoService {
    private final OrcamentoDAO orcamentoDAO;

    public OrcamentoService(OrcamentoDAO orcamentoDAO) {
        this.orcamentoDAO = orcamentoDAO;
    }

    public void criarOrcamento(Orcamento orcamento) throws SQLException {
        // Aqui poderiam entrar regras de negócio antes de salvar.
        // Por exemplo, validar se o orçamento tem itens ou se o total é positivo.
        orcamentoDAO.create(orcamento);
    }

    public void atualizarOrcamento(Orcamento orcamento) throws SQLException {
        orcamentoDAO.update(orcamento);
    }

    public void deletarOrcamento(int id) throws SQLException {
        orcamentoDAO.delete(id);
    }

    public List<Orcamento> listarOrcamentos() throws SQLException {
        return orcamentoDAO.readAll();
    }

    public List<OrcamentoItem> carregarItensOrcamento(int orcamentoId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return orcamentoDAO.carregarItensOrcamento(conn, orcamentoId);
        }
    }
}
