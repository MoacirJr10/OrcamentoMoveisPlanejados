package service;

import dao.ItemDAO;
import model.Item;

import java.sql.SQLException;
import java.util.List;

public class ItemService {
    private final ItemDAO itemDAO;

    public ItemService(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    public void cadastrarItem(Item item) throws SQLException {
        itemDAO.create(item);
    }

    public List<Item> listarItens() throws SQLException {
        return itemDAO.readAll();
    }

    public void atualizarItem(Item item) throws SQLException {
        itemDAO.update(item);
    }

    public void deletarItem(int id) throws SQLException {
        itemDAO.delete(id);
    }
}
