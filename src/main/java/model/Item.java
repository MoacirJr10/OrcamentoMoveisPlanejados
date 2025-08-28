package model;

public class Item {
    private int id;
    private String descricao;
    private double precoUnitario;
    private String dimensao = ""; // Novo campo para dimensão (opcional)

    // Construtores
    public Item() {}
    public Item(String descricao, double precoUnitario, String dimensao) {
        this.descricao = descricao;
        this.precoUnitario = precoUnitario;
        this.dimensao = dimensao != null ? dimensao.trim() : "";
    }

    public Item(String descricao, double precoUnitario) {
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }
    public String getDimensao() { return dimensao; }
    public void setDimensao(String dimensao) { this.dimensao = dimensao != null ? dimensao.trim() : ""; }


    @Override
    public String toString() {
        String dim = dimensao.isEmpty() ? "" : " (" + dimensao + ")";
        return descricao + dim + " - R$ " + precoUnitario;
    }

}