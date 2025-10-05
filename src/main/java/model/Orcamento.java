package model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Orcamento {
    private int id;
    private Cliente cliente;
    private Date data;
    private final List<OrcamentoItem> itens = new ArrayList<>();
    private BigDecimal total;

    public Orcamento() {
        this.total = BigDecimal.ZERO;
    }

    public Orcamento(Cliente cliente, Date data) {
        this.cliente = cliente;
        this.data = data;
        this.total = BigDecimal.ZERO;
    }

    public void adicionarItem(Item item, int quantidade) {
        if (item == null || quantidade <= 0) {
            return; // Não adiciona itens inválidos
        }
        itens.add(new OrcamentoItem(item, quantidade));
        calcularTotal();
    }

    public void calcularTotal() {
        this.total = itens.stream()
                .map(oi -> oi.getItem().getPrecoUnitario().multiply(new BigDecimal(oi.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public List<OrcamentoItem> getItens() { return itens; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) {
        this.total = (total != null && total.compareTo(BigDecimal.ZERO) >= 0) ? total : BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String clienteNome = (cliente != null) ? cliente.getNome() : "N/A";
        return "ID: " + id + " | Cliente: " + clienteNome + " | Total: " + currencyFormat.format(total);
    }
}
