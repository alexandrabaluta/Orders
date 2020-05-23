package Model;

public class Orders {
    private int orderID;
    private int clientID;
    private int productID;


    public Orders(int orderID, int clientID, int productID) {
        this.orderID = orderID;
        this.clientID = clientID;
        this.productID = productID;

    }

    public Orders(int clientID, int productID) {
        this.clientID = clientID;
        this.productID = productID;

    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }


}
