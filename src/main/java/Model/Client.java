package Model;

public class Client {
    private int clientID;
    private String name;
    private String address;
    private int age;

    public Client(int clientID, String name, String address, int age) {
        this.clientID = clientID;
        this.name = name;
        this.address = address;
        this.age = age;
    }

    public Client(String name, String address, int age) {
        this.name = name;
        this.address = address;
        this.age = age;
    }

    public Client(){}

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientID=" + clientID +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", age=" + age +
                '}';
    }
}
