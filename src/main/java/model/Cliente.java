package model;

public class Cliente {
    private int id;
    private String nome;
    private String telefone;
    private String endereco;

    public Cliente() {}
    public Cliente(String nome, String telefone, String endereco) {
        setNome(nome);
        setTelefone(telefone);
        setEndereco(endereco);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) {
        if (telefone == null || !telefone.matches("^\\+\\d{10,15}$")) {
            throw new IllegalArgumentException("Telefone deve estar no formato internacional, ex.: +5511999999999");
        }
        this.telefone = telefone;
    }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) {
        this.endereco = (endereco != null) ? endereco.trim() : "";
    }

    @Override
    public String toString() {
        return nome + " - Tel: " + telefone;
    }
}