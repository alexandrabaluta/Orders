package Model;

public class Order {
    private int orderID;
    private int clientID;
    private int productID;
    private int shippingID;

    public Order(int orderID, int clientID, int productID, int shippingID) {
        this.orderID = orderID;
        this.clientID = clientID;
        this.productID = productID;
        this.shippingID = shippingID;
    }

    public Order(int clientID, int productID, int shippingID) {
        this.clientID = clientID;
        this.productID = productID;
        this.shippingID = shippingID;
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

    public int getShippingID() {
        return shippingID;
    }

    public void setShippingID(int shippingID) {
        this.shippingID = shippingID;
    }
}
