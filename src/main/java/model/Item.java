package model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class Item {
    private int id;
    private String descricao;
    private BigDecimal precoUnitario;
    private String dimensao = "";

    public Item() {
        this.precoUnitario = BigDecimal.ZERO;
    }

    public Item(String descricao, BigDecimal precoUnitario, String dimensao) {
        this.descricao = descricao;
        this.precoUnitario = precoUnitario;
        this.dimensao = dimensao != null ? dimensao.trim() : "";
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) {
        // Garante que o preço nunca seja nulo
        this.precoUnitario = (precoUnitario != null && precoUnitario.compareTo(BigDecimal.ZERO) > 0) ? precoUnitario : BigDecimal.ZERO;
    }

    public String getDimensao() { return dimensao; }
    public void setDimensao(String dimensao) { this.dimensao = dimensao != null ? dimensao.trim() : ""; }

    @Override
    public String toString() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String dim = (dimensao != null && !dimensao.isEmpty()) ? " (" + dimensao + ")" : "";
        return descricao + dim + " - " + currencyFormat.format(precoUnitario);
    }
}
