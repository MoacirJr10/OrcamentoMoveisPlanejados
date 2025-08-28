import dao.DatabaseConnection;
import view.MainView;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        try {
            criarTabelas();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainView view = new MainView();
            view.setVisible(true);
        });
    }

    private static void criarTabelas() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS clientes (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nome VARCHAR(255), " +
                    "telefone VARCHAR(20), " +
                    "endereco VARCHAR(255))");

            stmt.execute("CREATE TABLE IF NOT EXISTS itens (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "descricao VARCHAR(255), " +
                    "preco_unitario DOUBLE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS orcamentos (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "cliente_id INT, " +
                    "data DATE, " +
                    "total DOUBLE, " +
                    "FOREIGN KEY (cliente_id) REFERENCES clientes(id))");

            stmt.execute("CREATE TABLE IF NOT EXISTS orcamento_itens (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "orcamento_id INT, " +
                    "item_id INT, " +
                    "quantidade INT, " +
                    "FOREIGN KEY (orcamento_id) REFERENCES orcamentos(id), " +
                    "FOREIGN KEY (item_id) REFERENCES itens(id))");
        }
    }
}