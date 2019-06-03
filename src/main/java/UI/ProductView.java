package UI;

import Controller.Controller;
import Repository.AbstractRepoProduct;
import Model.Product;
import Database.DatabaseConnection;

import javax.swing.*;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ProductView extends JFrame implements ActionListener{
    static JFrame f;
    Controller controller = new Controller();

    JButton addProduct;
    JButton deleteProduct;
    JButton update;
    JButton placeOrder;

    final JTextField tf1;
    final JTextField tf2;
    final JTextField tf4;
    final JTextField tf5;

    JLabel l1, l2, l4, l5;

    DefaultTableModel model = new DefaultTableModel();
    JTable table = new JTable(model);

    public ProductView() {
        f = new JFrame("ORDER MANAGEMENT - PRODUCT");

        addProduct = new JButton("ADD PRODUCT");
        addProduct.setBounds(400, 90, 150, 30);
        addProduct.addActionListener(this);

        placeOrder = new JButton("PLACE ORDER");
        placeOrder.setBounds(400, 300, 150, 30);
        placeOrder.addActionListener(this);

        deleteProduct = new JButton("DELETE PRODUCT");
        deleteProduct.setBounds(400, 160, 150, 30);
        deleteProduct.addActionListener(this);

        update = new JButton("UPDATE PRODUCT");
        update.setBounds(400, 230, 150, 30);
        update.addActionListener(this);

        tf1 = new JTextField();
        tf1.setBounds(180, 100, 150, 30);

        tf2 = new JTextField();
        tf2.setBounds(180, 160, 150, 30);

        tf4 = new JTextField();
        tf4.setBounds(180, 220, 150, 30);

        tf5 = new JTextField();
        tf5.setBounds(280, 280, 50, 30);

        l1 = new JLabel("NAME");
        l1.setBounds(73, 100, 150, 30);

        l2 = new JLabel("PRICE");
        l2.setBounds(73, 160, 150, 30);

        l4 = new JLabel("QUANTITY");
        l4.setBounds(73, 220, 150, 30);

        l5 = new JLabel("SHIPPING TYPE:");
        l5.setBounds(73, 280, 200, 30);

        ArrayList<String> tableColumns = new ArrayList<String>();
        tableColumns = getFields(new Product());
        for (int i=0; i<tableColumns.size(); i++){
            model.addColumn(tableColumns.get(i));
        }

        try {
            DatabaseConnection connectionFactory = new DatabaseConnection();
            Connection con = connectionFactory.getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT * FROM product");
            ResultSet Rs = statement.executeQuery();
            while(Rs.next()){
                model.addRow(new Object[]{Rs.getInt(1), Rs.getString(2),Rs.getFloat(3),Rs.getInt(4)});
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        f.add(addProduct);
        f.add(deleteProduct);
        f.add(update);
        f.add(placeOrder);

        f.add(tf1);
        f.add(tf2);
        f.add(tf4);
        f.add(tf5);

        f.add(l1);
        f.add(l2);
        f.add(l4);
        f.add(l5);

        JScrollPane jsp = new JScrollPane(table);
        jsp.setBounds(600, 100, 400, 400);

        f.add(jsp);

        f.setSize(1600, 700);
        f.setLayout(null);
        f.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Method that performs the update of the selected row.
     * It replaces the fields with the corresponding data form the JTextFields.
     * @param idToUpdate is the id of the product in this case that we will update. Only the id is left unchanged.
     */

    public void updateRow(int idToUpdate){
        try{
            DatabaseConnection connectionFactory = new DatabaseConnection();
            Connection con = connectionFactory.getConnection();
            PreparedStatement statement = con.prepareStatement("UPDATE product " +
                    "SET name = ?, price = ?, quantity = ? " +
                    "WHERE productId = ?");
            statement.setInt(1, Integer.parseInt(tf1.getText()));
            statement.setString(2, tf2.getText());
            statement.setFloat(3, Float.parseFloat(tf4.getText()));
            statement.setInt(4, idToUpdate);
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Method to perform the update of the table after every action (insert, delete and update).
     * Very useful to see the changes done in real time and not after closing and reopening the application.
     */
    private void tableRefresh(){
        model.getDataVector().removeAllElements();
        try {
            DatabaseConnection connectionFactory = new DatabaseConnection();
            Connection con = connectionFactory.getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT * FROM product");
            ResultSet Rs = statement.executeQuery();
            while(Rs.next()){
                model.addRow(new Object[]{Rs.getInt(1), Rs.getString(2),Rs.getFloat(3),Rs.getInt(4)});
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This is a method that builds the table header using reflection
     * @param object
     * @return an array list of Strings containing the field names declared in the Product's class
     */
    private ArrayList<String> getFields(Object object){
        ArrayList<String> fieldNames = new ArrayList<String>();
        Class objClass = object.getClass();
        Field[] fields = objClass.getDeclaredFields();
        for (Field f: fields){
            fieldNames.add(f.getName());
        }
        return fieldNames;
    }

    /**
     * This is a method that gives the buttons the corresponding actions to perform when they are pressed.
     * Also int this function I have done the linkage with the ClientGUI class that will help at placing an order.
     * We have to select the client from the ClientGUI, insert the desired amount we want to buy, the shipping type (fast
     * or standard) and then select the product we want to buy. If everything is ok (enough stock) then the order will
     * be placed. If we have a problem: our quantity to buy is greater than the stock, a pop-up message will appear
     * letting the user know that there is a stock problem.
     * @param e is the ActionEvent characteristic to every button
     */
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == addProduct){
            Product product = new Product(tf1.getText(), Float.parseFloat(tf4.getText()), Integer.parseInt(tf5.getText()));
            AbstractRepoProduct repoProduct = new AbstractRepoProduct();
            repoProduct.insert(product);
            tableRefresh();
        }else if(e.getSource() == deleteProduct){
            int row = table.getSelectedRow();
            int deleteId = Integer.parseInt(table.getValueAt(row, 0).toString());
            AbstractRepoProduct repoProduct = new AbstractRepoProduct();
            repoProduct.delete(deleteId);
            tableRefresh();
        }else if(e.getSource() == update){
            int row = table.getSelectedRow();
            int idToUpdate = Integer.parseInt(table.getValueAt(row, 0).toString());
            String name = tf1.getText();
            String price = tf2.getText();
            String quantity = tf4.getText();

            if(name.equals(""))
            {
                name = table.getValueAt(row, 1).toString();
            }
            if(price.equals(""))
            {
                price = table.getValueAt(row, 2).toString();
            }
            if(tf4.getText().equals(""))
            {
                quantity = table.getValueAt(row, 3).toString();
            }
            Product product = new Product(idToUpdate,name, Float.parseFloat(price), Integer.parseInt(quantity));
            controller.updateProduct(product);
            updateRow(idToUpdate);

            tableRefresh();
        }else if(e.getSource() == placeOrder){
            int clientId = ClientView.getSelectedId();
            int row = table.getSelectedRow();
            int productId = Integer.parseInt(table.getValueAt(row, 0).toString());
            int shippingId = 0;
            if (tf5.getText().equals("1")){
                shippingId = 1;
            }else{
                shippingId = 2;
            }
            int quantity = Integer.parseInt(tf4.getText());
            float price = Float.parseFloat(table.getValueAt(row, 2).toString());
            float totalPrice = quantity * price;
            int stock = Integer.parseInt(table.getValueAt(row, 3).toString());
            if (quantity > stock){
                JOptionPane.showMessageDialog(this, "Stock too low", "Stock error", JOptionPane.ERROR_MESSAGE);
            }else {
                table.setValueAt(stock-quantity, row, 3);
                //Order order = new Order(clientId, productId, shippingId);
                controller.orderProduct(clientId,productId,quantity,shippingId);
                //AbstractDAO orderDAO = new AbstractDAO();
                //orderDAO.insert(order);
            }
        }
    }
}