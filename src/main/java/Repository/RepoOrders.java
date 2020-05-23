package Repository;

import Database.DatabaseConnection;
import Model.Client;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RepoOrders {

    protected static final Logger LOGGER = Logger.getLogger(RepoOrders.class.getName());
    private final static String checkStatementString = "SELECT quantity FROM Product WHERE productID = ? ";
    private final static String addStatementString = "INSERT INTO Orders(clientID, productID) " + "VALUES (?, ?);";
    private final static String updateStatementString = "UPDATE Product SET Quantity = ? WHERE productID = ? ; ";

    /**
     * Verifica in tabelul Orders daca cantitatea dorita de client
     * este disponibila.
     * @param productID - id-ul produsului dorit
     * @param quantity - cantitatea dorita
     * @return 0 - daca cantitatea dorita nu este disponibila;
     *         cantitatea disponibila - daca cantitatea dorita este
     *         disponibila
     * */
    public static int checkUnderStock(int quantity, int productID){
        int availableQuantity = 0;
        Connection dbConnection = DatabaseConnection.getConnection();
        PreparedStatement findStatement = null;
        ResultSet rs = null;
        try {
            findStatement = dbConnection.prepareStatement(checkStatementString);
            findStatement.setInt(1, productID);
            rs = findStatement.executeQuery();
            rs.next();

            availableQuantity = rs.getInt("Quantity");
            if(! (quantity <= availableQuantity)){
                availableQuantity = 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING,"ClientDAO:Under-stock message " + e.getMessage());
            return 0;
        } finally {
            DatabaseConnection.close(rs);
            DatabaseConnection.close(findStatement);
            DatabaseConnection.close(dbConnection);
        }
        return availableQuantity;
    }

    public static void addOrder(int clientID, int productID, int quantity) throws SQLException {
        Connection dbConnection = DatabaseConnection.getConnection();
        int cantitateVeche = checkUnderStock(quantity,productID);
        int cantitateNoua = cantitateVeche - quantity;
        //se executa doar daca cantitatea introdusa e buna
        if( cantitateVeche != 0) {
            PreparedStatement insertStatement = null;
            try {
                insertStatement = dbConnection.prepareStatement(addStatementString, Statement.RETURN_GENERATED_KEYS);
                insertStatement.setInt(1, clientID);
                insertStatement.setInt(2, productID);
                insertStatement.executeUpdate();

            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "ClientDAO:insert order " + e.getMessage());
            } finally {
                DatabaseConnection.close(insertStatement);
                DatabaseConnection.close(dbConnection);
            }
            updateProducts(cantitateNoua,productID);
            /*Connection dbConnection1 = DatabaseConnection.getConnection();
            String query= "SELECT name FROM Client WHERE clientID ='" + clientID + "' ";
            Statement st= dbConnection1.createStatement();
            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                String name= rs.getString("name");
                System.out.println(name);
            } */
        }
    }

    /**
     * Actualizeaza cantitatea ramasa a produsului comandat de client
     * (actualizarea din tabelul Product)
     * */
    public static void updateProducts(int newQuantity, int productID){
        Connection dbConnection = DatabaseConnection.getConnection();
        PreparedStatement insertStatement = null;
        try {
            insertStatement = dbConnection.prepareStatement(updateStatementString, Statement.RETURN_GENERATED_KEYS);
            insertStatement.setInt(1, newQuantity);
            insertStatement.setInt(2, productID);
            insertStatement.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "ClientDAO:update quantity " + e.getMessage());
        } finally {
            DatabaseConnection.close(insertStatement);
            DatabaseConnection.close(dbConnection);
        }
    }
}
