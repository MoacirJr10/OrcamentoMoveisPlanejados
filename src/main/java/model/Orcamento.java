package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Orcamento {
    private int id;
    private Cliente cliente;
    private Date data;
    private List<OrcamentoItem> itens = new ArrayList<>();
    private double total;

    // Construtores
    public Orcamento() {}
    public Orcamento(Cliente cliente, Date data) {
        this.cliente = cliente;
        this.data = data;
    }

    public void adicionarItem(Item item, int quantidade) {
        itens.add(new OrcamentoItem(item, quantidade));
        calcularTotal();
    }

    private void calcularTotal() {
        total = itens.stream().mapToDouble(oi -> oi.getItem().getPrecoUnitario() * oi.getQuantidade()).sum();
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }
    public List<OrcamentoItem> getItens() { return itens; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    @Override
    public String toString() {
        return "Orçamento ID: " + id + " - Cliente: " + (cliente != null ? cliente.getNome() : "N/A") + " - Total: R$ " + total;
    }
}