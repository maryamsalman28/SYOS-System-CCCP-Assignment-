package service;

import dao.MultiStoreDAO;
import model.MultiStore;

import java.util.List;

public class MultiStoreService {
    private MultiStoreDAO multiStoreDAO = new MultiStoreDAO();

    public void addStore(MultiStore store) {
        multiStoreDAO.addStore(store);
    }

    public List<MultiStore> getAllStores() {
        return multiStoreDAO.getAllStores();
    }
}
