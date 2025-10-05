-- Tabela de Clientes
CREATE TABLE IF NOT EXISTS clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) UNIQUE, -- Telefone deve ser único
    endereco VARCHAR(255)
);

-- Tabela de Itens
CREATE TABLE IF NOT EXISTS itens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    -- Usar DECIMAL para valores monetários para evitar erros de arredondamento
    preco_unitario DECIMAL(10, 2) NOT NULL CHECK (preco_unitario >= 0),
    dimensao VARCHAR(50)
);

-- Tabela de Orçamentos
CREATE TABLE IF NOT EXISTS orcamentos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    data DATE NOT NULL,
    -- Usar DECIMAL para valores monetários
    total DECIMAL(10, 2) NOT NULL CHECK (total >= 0),
    -- Se um cliente for deletado, todos os seus orçamentos também serão
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
);

-- Tabela de associação entre Orçamentos e Itens
CREATE TABLE IF NOT EXISTS orcamento_itens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    orcamento_id INT NOT NULL,
    item_id INT NOT NULL,
    quantidade INT NOT NULL CHECK (quantidade > 0),
    -- Se um orçamento for deletado, seus itens também serão
    FOREIGN KEY (orcamento_id) REFERENCES orcamentos(id) ON DELETE CASCADE,
    -- Impede que um item seja deletado se estiver em uso em algum orçamento
    FOREIGN KEY (item_id) REFERENCES itens(id) ON DELETE RESTRICT
);
