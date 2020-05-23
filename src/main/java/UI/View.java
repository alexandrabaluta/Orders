package UI;
import Controller.Controller;
import Database.DatabaseConnection;
import Model.Client;
import Model.Product;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;



public class View {


    private Controller controller = new Controller();
    public View() throws SQLException {

    }
    private void showMenu() {
        System.out.print("MENU");
        System.out.println("_______________________________________________________________________________________");
        System.out.println("Option:");
        System.out.println("1. All clients");
        System.out.println("2. Add client ");
        System.out.println("3. Delete client ");
        System.out.println("4. All products ");
        System.out.println("5. Add product ");
        System.out.println("6. Delete product ");
        System.out.println("7. Add orders ");
        System.out.println("0. Exit");



}

   public void start() throws SQLException {
        showMenu();
       Scanner scanner = new Scanner(System.in);
       String input;
       while (!(input = scanner.nextLine()).equals("0")) {
           assert false;
           switch (input) {
               default:
                   break;

               case "1":
                   List<Client> clients = controller.findAllClients();
                   for (Client client : clients) {
                       System.out.println(client);
                   }
                   break;
               case "2":
                   Scanner in = new Scanner(System.in);
                   System.out.print("Name: ");
                   String name = in.next();
                   System.out.print("Andress: ");
                   String adress = in.next();
                   System.out.print("Age: ");
                   String age = in.next();
                   Client client = new Client(name, adress, Integer.parseInt(age));
                   controller.insertClient(client);
                   break;
               case "3":
                   Scanner in2 = new Scanner(System.in);
                   System.out.print("id-ul clientului care trebuie sters: ");
                   int id1 = in2.nextInt();
                   controller.deleteClient(id1);
                   break;
               case "4":
                   List<Product> products = controller.findAllProducts();
                   for (Product product : products) {
                       System.out.println(product);
                   }
                   break;
               case "5":
                   Scanner in4 = new Scanner(System.in);
                   System.out.print("Name: ");
                   String name1 = in4.next();
                   System.out.print("Price: ");
                   String price = in4.next();
                   System.out.print("Quantity: ");
                   String quantity = in4.next();
                   Product product = new Product(name1, Integer.parseInt(price), Integer.parseInt(quantity));
                   System.out.println("Succesfully added!");
               case "6":
                   Scanner in5 = new Scanner(System.in);
                   System.out.print("id-ul produsului care trebuie sters: ");
                   int id2 = in5.nextInt();
                   controller.deleteProduct(id2);
                   System.out.println("Succesfully deleted!");
                   break;
               case "7":
                   Scanner in6 = new Scanner(System.in);
                   System.out.print("ID client: ");
                   int idC = in6.nextInt();
                   System.out.print("ID product: ");
                   int idP = in6.nextInt();
                   System.out.print("Quantity: ");
                   int quantity1 = in6.nextInt();
                   controller.orderProduct(idC, idP, quantity1);
                   Connection dbConnection1 = DatabaseConnection.getConnection();
                   String query = "SELECT name FROM Client WHERE clientID ='" + idC + "' ";
                   Statement st = dbConnection1.createStatement();
                   ResultSet rs = st.executeQuery(query);
                   while (rs.next()) {
                       String name3 = rs.getString("name");
                       System.out.println(name3);

                       System.out.println("Succesfully added!");

                   }

           }

       }
   }
}

