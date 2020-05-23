package Controller;

import Model.Client;
import Model.Product;
import Repository.*;

import java.sql.SQLException;
import java.util.List;

public class Controller {
    private AbstractRepoClient client = new AbstractRepoClient();
    private AbstractRepoProduct product = new AbstractRepoProduct();
    private RepoOrders orders = new RepoOrders();
    public Controller() {
    }

    public List<Client> findAllClients(){
        return client.findAll();
    }

    public Client findClientById(int id){
        return client.findById(id);
    }

    public int insertClient(Client c){
        return client.insert(c);
    }

    public int updateClient(Client c){
        return client.update(c);
    }

    public boolean deleteClient(int id){
        return client.delete(id);
    }

    public List<Product> findAllProducts(){
        return product.findAll();
    }

    public Product findProductById(int id){
        return product.findById(id);
    }

    public int insertProduct(Product p){
        return product.insert(p);
    }

    public int updateProduct(Product p){
        return product.update(p);
    }

    public boolean deleteProduct(int id){
        return product.delete(id);
    }

    public void orderProduct(int clientID, int productID, int quantity) throws SQLException {
        orders.addOrder(clientID,  productID,  quantity);
    }
}
