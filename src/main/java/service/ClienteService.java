package service;

import dao.ClienteDAO;
import model.Cliente;

import java.sql.SQLException;
import java.util.List;

public class ClienteService {
    private final ClienteDAO clienteDAO;

    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public void cadastrarCliente(Cliente cliente) throws SQLException {
        clienteDAO.create(cliente);
    }

    public List<Cliente> listarClientes() throws SQLException {
        return clienteDAO.readAll();
    }

    public void atualizarCliente(Cliente cliente) throws SQLException {
        clienteDAO.update(cliente);
    }

    public void deletarCliente(int id) throws SQLException {
        clienteDAO.delete(id);
    }
}
