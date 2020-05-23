package Repository;

import Database.DatabaseConnection;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AbstractDAO<T> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

    private final Class<T> type;

    @SuppressWarnings("unchecked")
    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * field  = PK-ul tabelului
     * */
    private String createSelectQuery(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");;
        sb.append(type.getSimpleName());
        sb.append(" WHERE " + field + " =?");
        return sb.toString();
    }

    /**
     * field = PK-ul tabelului
     * */
    private String createFindAllQuery(){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(type.getSimpleName());
        return sb.toString();
    }

    /**
     * String[] fields - coloanele din tabel
     * */
    private String createInsertQuery(List<String> fields){
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(type.getSimpleName());
        sb.append("(");
        for(int j = 0; j< fields.size()-1;j++){
            sb.append(fields.get(j));
            sb.append(",");
        }
        sb.append(fields.get(fields.size()-1));
        sb.append(") VALUES (");
        for(int i = 0; i < fields.size()-1; i++){
            sb.append("?,");
        }
        sb.append("?)");
        return sb.toString();
    }

    /**
     * field - PK-ul tabelului/id-ul
     * */
    private String createDeleteQuery(String field){
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(type.getSimpleName());
        sb.append(" WHERE ");
        sb.append(type.getSimpleName());
        sb.append(field);
        sb.append(" = ?");
        return sb.toString();
    }

    /**
     * String[] fields - lista coloanelor (exceptand PK)
     * String field - PK
     * */
    private String createUpdateQuery(List<String> fields){
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append(type.getSimpleName());
        sb.append(" SET ");
        for(int i = 1; i<fields.size()-1 ; i++){ //incep de la 1 ca sa evit cheia primara de pe poz 0
            sb.append(fields.get(i));
            sb.append(" = ?,");
        }
        sb.append(fields.get(fields.size()-1));
        sb.append(" = ? ");
        sb.append("WHERE ID");
        //sb.append(fields.get(0)); //fields[0] contine PK-ul tabelului
        sb.append(" = ?");
        return sb.toString();
    }

    //returneaza toate elementele din tabel
    public List<T> findAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createFindAllQuery();

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            if(resultSet != null)
                return createObjects(resultSet); //returneaza lista de obiecte
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
        } finally {
            DatabaseConnection.close(resultSet);
            DatabaseConnection.close(statement);
            DatabaseConnection.close(connection);
        }
        return null;
    }

    //cauta un element in tabel
    public T findById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery("id");
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next())
                return createObjects(resultSet).get(0);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
        } finally {
            DatabaseConnection.close(resultSet);
            DatabaseConnection.close(statement);
            DatabaseConnection.close(connection);
        }
        return null;
    }

    //returneaza toate coloanele tabelului, mai putin PK-ul
    public List<String> getTableColumns(){
        List<String> fields = new ArrayList<String>();
        for (Field field: type.getDeclaredFields()){
            String fieldName = field.getName();
            //daca getter-ul ii corespunde id-ului, atunci nu il introduce in lista
            if(!fieldName.contains("ID"))
                fields.add(fieldName);
        }
        return fields;
    }

    //returneaza TOATE COLOANELE tabelului
    public List<String> getAllTableColumns(){
        List<String> fields = new ArrayList<String>();
        for (Field field: type.getDeclaredFields()){
            String fieldName = field.getName();
            fields.add(fieldName);
        }
        return fields;
    }

    private List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<T>();

        try {
            while (resultSet.next()) {
                T instance = type.newInstance();
                for (Field field : type.getDeclaredFields()) {
                    Object value = resultSet.getObject(field.getName());
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), type);
                    Method method = propertyDescriptor.getWriteMethod();
                    method.invoke(instance, value);
                }
                list.add(instance);
            }
        } catch (InstantiationException | IntrospectionException | IllegalAccessException | SecurityException | IllegalArgumentException | InvocationTargetException | SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * @param o - obiectul care trebuie inserat
     * @return ID-ul obiectului, daca acesta a fost inserat cu succes.
     *          -1, daca obiectul nu a fost inserat in bd
     * */
    public int insert(Object o){
        int insertedId = -1;
        Connection connection = null;
        PreparedStatement statement = null;
        List<String> columnNames = getTableColumns();
        String query = createInsertQuery(columnNames);
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            Field[] fields = type.getDeclaredFields();
            List<Field> fieldsWithoutPK = new ArrayList<Field>();
            for(int i = 0; i< fields.length;i++){
                if(i!=0)
                    fieldsWithoutPK.add(fields[i]);
            }
            for(int i=0;i<fieldsWithoutPK.size();i++)
            {
                Field field=fieldsWithoutPK.get(i);
                field.setAccessible(true);
                Object value = field.get(o);
                statement.setObject((i+1), value);
            }
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                insertedId = rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:insert " + e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(statement);
            DatabaseConnection.close(connection);
        }
        return insertedId;
    }

    //actualizeaza un element din baza de date
    public int update(Object o) {
        int updated = 0;
        Connection connection = null;
        PreparedStatement statement = null;
        List<String> columnNames = getAllTableColumns();
        String query = createUpdateQuery(columnNames);
        System.out.println(query);
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            Field[] fields = type.getDeclaredFields();
            for(int i=1;i<fields.length;i++)
            {
                Field field=fields[i];
                field.setAccessible(true);
                Object value = field.get(o);
                statement.setObject((i), value);
            }
            Field field=fields[0];
            field.setAccessible(true);
            Object value = field.get(o);
            statement.setObject(fields.length, value);
            updated = statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:update " + e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(statement);
            DatabaseConnection.close(connection);
        }
        return updated;
    }

    public boolean delete(int id){
        int deleted = 0;
        Connection connection = null;
        PreparedStatement statement = null;
        String query = createDeleteQuery("id");
        System.out.println(query);
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1,id);
            deleted = statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:delete " + e.getMessage());
        } finally {
            DatabaseConnection.close(statement);
            DatabaseConnection.close(connection);
        }
        if(deleted == 1)
            return true;
        else
            return false;
    }
}
