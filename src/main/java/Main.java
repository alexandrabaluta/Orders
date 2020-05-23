import Controller.Controller;
import Database.DatabaseConnection;
import Model.Client;
import Model.Product;
import UI.View;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        View view= new View();
        view.start();
    }

}





