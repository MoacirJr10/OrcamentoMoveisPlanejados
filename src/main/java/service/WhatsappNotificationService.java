package service;

import model.Item;
import model.Orcamento;
import model.OrcamentoItem;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class WhatsappNotificationService {

    private static final DecimalFormat df = new DecimalFormat("#,##0.00"); // Formato R$ 300,00
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); // Formato data

    public void enviarOrcamento(Orcamento orcamento) {
        String mensagem = gerarMensagemOrcamento(orcamento);
        String telefone = orcamento.getCliente().getTelefone().replaceAll("[^\\d]", "");
        String url = "https://wa.me/" + telefone + "?text=" + URLEncoder.encode(mensagem, StandardCharsets.UTF_8);

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI.create(url));
            }
        } catch (IOException e) {
            // Em uma aplicação real, usaríamos um framework de log
            System.err.println("Erro ao abrir o WhatsApp: " + e.getMessage());
        }
    }

    public String gerarMensagemOrcamento(Orcamento orcamento) {
        StringBuilder sb = new StringBuilder();
        sb.append("*Orçamento de Móveis Planejados*\n\n");
        sb.append("*Cliente:* ").append(orcamento.getCliente().getNome()).append("\n");
        sb.append("*Telefone:* ").append(orcamento.getCliente().getTelefone()).append("\n");
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
}
