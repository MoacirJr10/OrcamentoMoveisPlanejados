package service;

import dao.ClienteDAO;
import dao.ItemDAO;
import dao.OrcamentoDAO;
import model.Cliente;
import model.Item;
import model.Orcamento;
import model.OrcamentoItem;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrcamentoService {
    private ClienteDAO clienteDAO = new ClienteDAO();
    private ItemDAO itemDAO = new ItemDAO();
    private OrcamentoDAO orcamentoDAO = new OrcamentoDAO();
    private static final DecimalFormat df = new DecimalFormat("#,##0.00"); // Formato R$ 300,00
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); // Formato data

    public void cadastrarCliente(Cliente cliente) throws SQLException {
        clienteDAO.create(cliente);
    }

    public List<Cliente> listarClientes() throws SQLException {
        return clienteDAO.readAll();
    }

    public void cadastrarItem(Item item) throws SQLException {
        itemDAO.create(item);
    }

    public List<Item> listarItens() throws SQLException {
        return itemDAO.readAll();
    }

    public void criarOrcamento(Orcamento orcamento) throws SQLException {
        orcamentoDAO.create(orcamento);
    }

    public List<Orcamento> listarOrcamentos() throws SQLException {
        return orcamentoDAO.readAll();
    }

    // Enviar para um orçamento específico com itens selecionados
    public void enviarOrcamentoViaWhatsApp(Orcamento orcamento) {
        String mensagem = gerarMensagemOrcamento(orcamento);
        String telefone = orcamento.getCliente().getTelefone();
        String url = "https://wa.me/" + telefone + "?text=" + URLEncoder.encode(mensagem, StandardCharsets.UTF_8);

        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(URI.create(url));
        } catch (IOException e) {
            // Exibir mensagem de erro ao usuário em vez de apenas printStackTrace
            System.err.println("Erro ao abrir o WhatsApp: " + e.getMessage());
            // Você pode integrar isso com uma interface (ex.: JOptionPane) se desejar
        }
    }

    public String gerarMensagemOrcamento(Orcamento orcamento) {
        StringBuilder sb = new StringBuilder();
        sb.append("*Orçamento de Móveis Planejados*\n\n");
        sb.append("*Cliente:* ").append(orcamento.getCliente().getNome()).append("\n");
        sb.append("*Telefone:* ").append(orcamento.getCliente().getTelefone()).append("\n"); // Adicionado para separação clara
        sb.append("*Endereço:* ").append(orcamento.getCliente().getEndereco()).append("\n");
        sb.append("*Data:* ").append(sdf.format(orcamento.getData())).append("\n\n");
        sb.append("*Itens:*\n");
        for (OrcamentoItem oi : orcamento.getItens()) {
            Item item = oi.getItem();
            String dim = item.getDimensao() != null && !item.getDimensao().isEmpty() ? " (" + item.getDimensao() + ")" : "";
            double subtotal = item.getPrecoUnitario() * oi.getQuantidade();
            sb.append("- ").append(item.getDescricao()).append(dim)
                    .append(" x ").append(oi.getQuantidade())
                    .append(" = R$ ").append(df.format(subtotal)).append("\n");
        }
        sb.append("\n*Total:* R$ ").append(df.format(orcamento.getTotal())).append("\n\n");
        sb.append("Obrigado pela preferência! Entre em contato para mais detalhes.");
        return sb.toString();
    }

    // Novos métodos para update e delete
    public void atualizarItem(Item item) throws SQLException {
        itemDAO.update(item);
    }

    public void deletarItem(int id) throws SQLException {
        itemDAO.delete(id);
    }

    public void atualizarCliente(Cliente cliente) throws SQLException {
        clienteDAO.update(cliente);
    }

    public void deletarCliente(int id) throws SQLException {
        clienteDAO.delete(id);
    }
}