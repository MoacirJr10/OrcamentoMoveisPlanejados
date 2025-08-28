package model;

public class OrcamentoItem {
    private Item item;
    private int quantidade;

    public OrcamentoItem(Item item, int quantidade) {
        this.item = item;
        this.quantidade = quantidade;
    }

    public Item getItem() { return item; }
    public int getQuantidade() { return quantidade; }
}