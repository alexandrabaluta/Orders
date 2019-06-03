package UI;

import Controller.Controller;
import Database.DatabaseConnection;
import Model.Client;
import Repository.AbstractRepoClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ClientView extends JFrame implements ActionListener {
    static JFrame f;

    JButton addClient;
    JButton deleteClient;
    JButton update;

    final JTextField tf1;
    final JTextField tf3;
    final JTextField tf4;

    JLabel l1, l3, l4;

    static DefaultTableModel model = new DefaultTableModel();
    static JTable table = new JTable(model);

    public ClientView()
    {
        f = new JFrame("ORDER MANAGEMENT - CLIENT");

        addClient = new JButton("INSERT CLIENT");
        addClient.setBounds(400, 50, 110, 30);
        addClient.addActionListener(this);

        deleteClient = new JButton("DELETE CLIENT");
        deleteClient.setBounds(400, 100, 110, 30);
        deleteClient.addActionListener(this);

        update = new JButton("UPDATE CLIENT");
        update.setBounds(400, 150, 110, 30);
        update.addActionListener(this);

        f.add(addClient);
        f.add(deleteClient);
        f.add(update);

        tf1 = new JTextField();
        tf1.setBounds(180, 50, 150, 30);

        tf3 = new JTextField();
        tf3.setBounds(180, 100, 150, 30);

        tf4 = new JTextField();
        tf4.setBounds(180, 150, 150, 30);

        f.add(tf1);
        f.add(tf3);
        f.add(tf4);

        l1 = new JLabel("NAME");
        l1.setBounds(73, 50, 150, 30);

        l3 = new JLabel("ADDRESS");
        l3.setBounds(73, 100, 150, 30);

        l4 = new JLabel("AGE");
        l4.setBounds(73, 150, 150, 30);

        f.add(l1);
        f.add(l3);
        f.add(l4);

        ArrayList<String> tableColumns = new ArrayList<String>();
        tableColumns = getFields(new Client());
        for (int i=0; i<tableColumns.size(); i++) {
            model.addColumn(tableColumns.get(i));
        }

        try {
            DatabaseConnection connectionFactory = new DatabaseConnection();
            Connection con = connectionFactory.getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT * FROM client");
            ResultSet Rs = statement.executeQuery();
            while(Rs.next()) {
                model.addRow(new Object[]{
                        Rs.getInt(1),
                        Rs.getString(2),
                        Rs.getString(3),
                        Rs.getInt(4)});
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        JScrollPane jsp = new JScrollPane(table);
        jsp.setBounds(550, 100, 400, 400);

        f.add(jsp);

        f.setSize(1600, 700);
        f.setLayout(null);
        f.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Method that performs the update of the selected row.
     * It replaces the fields with the corresponding data form the JTextFields.
     * @param idToUpdate is the id of the client in this case that we will update. Only the id is left unchanged.
     */
    public void updateRow(int idToUpdate){
        try{
            DatabaseConnection connectionFactory = new DatabaseConnection();
            Connection con = connectionFactory.getConnection();
            PreparedStatement statement = con.prepareStatement("UPDATE client " +
                    "SET name = ?, address = ?, age = ? " +
                    "WHERE clientId = ?");
            statement.setString(1, tf1.getText());
            statement.setString(2, tf3.getText());
            statement.setInt(3, Integer.parseInt(tf4.getText()));
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
            PreparedStatement statement = con.prepareStatement("SELECT * FROM client");
            ResultSet Rs = statement.executeQuery();
            while(Rs.next()){
                model.addRow(new Object[]{Rs.getInt(1),
                        Rs.getString(2),
                        Rs.getString(3),
                        Rs.getInt(4)});
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This is a method that builds the table header using reflection
     * @param object
     * @return an array list of Strings containing the field names declared in the Object's class
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
     * This is a method that gives the buttons the corresponding actions to perform when they are pressed
     * @param e is the ActionEvent characteristic to every button
     */
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == addClient){
            Client client = new Client(tf1.getText(), tf3.getText(), Integer.parseInt(tf4.getText()));
            AbstractRepoClient repoClient = new AbstractRepoClient();
            repoClient.insert(client);
            tableRefresh();
        }else if(e.getSource() == deleteClient){
            int row = table.getSelectedRow();
            int deleteId = Integer.parseInt(table.getValueAt(row, 0).toString());
            AbstractRepoClient repoClient = new AbstractRepoClient();
            repoClient.delete(deleteId);
            tableRefresh();
        }else if(e.getSource() == update){
            int row = table.getSelectedRow();
            int idToUpdate = Integer.parseInt(table.getValueAt(row, 0).toString());
            String name = tf1.getText();
            String address = tf3.getText();
            String age = tf4.getText();

            if(name.equals(""))
            {
                name = table.getValueAt(row, 1).toString();
            }
            if(address.equals(""))
            {
                address = table.getValueAt(row, 2).toString();
            }
            if(tf4.getText().equals(""))
            {
                age = table.getValueAt(row, 3).toString();
            }
            Controller controller = new Controller();
            Client client = new Client(idToUpdate,name, address,Integer.parseInt(age));
            controller.updateClient(client);
            updateRow(idToUpdate);
            tableRefresh();
        }
    }

    /**
     * Getter used to take the selected client id from the JTable, method which will be used in ProductView so that we
     * will be able to place an order. This is the linkage between the client's table from the ClientView and the
     * product's table in the ProductView
     * @return selectedId is the clientId from the client table in the database
     */
    public static int getSelectedId(){
        int row = table.getSelectedRow();
        int selectedId = Integer.parseInt(table.getValueAt(row, 0).toString());
        return selectedId;
    }

}
