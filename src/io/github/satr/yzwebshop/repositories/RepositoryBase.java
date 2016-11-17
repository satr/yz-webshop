package io.github.satr.yzwebshop.repositories;

import javax.servlet.ServletException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public abstract class RepositoryBase<T> {
    String url = "jdbc:mysql://localhost/dev1";
    String user = "user1";
    String password = "qwerty";

    public RepositoryBase() {
        try{
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }

    public List<T> getList() throws ServletException {
        try {
            ArrayList<T> list = new ArrayList<>();
            try(Connection connection = DriverManager.getConnection(url, user, password);
                Statement sqlStatement = connection.createStatement();
                ResultSet resultSet = sqlStatement.executeQuery(getSqlForList());) {
                while(resultSet.next()) {
                    addEntity(list, resultSet);
                }
                return list;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new ServletException(ex);
        }
    }

    protected abstract String getSqlForList();

    protected abstract void addEntity(List<T> list, ResultSet resultSet) throws SQLException;

    public T get(int id) throws ServletException {
        try {
            try(Connection connection = DriverManager.getConnection(url, user, password);
                PreparedStatement sqlStatement = prepareStatementForGetEntityById(connection, id);
                ResultSet resultSet = sqlStatement.executeQuery()) {
                return resultSet.next() ? getEntity(resultSet) : null;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new ServletException(ex);
        }
    }

    private PreparedStatement prepareStatementForGetEntityById(Connection connection, int id) throws SQLException {
        PreparedStatement sqlStatement = connection.prepareStatement(getSqlForSingle());
        sqlStatement.setInt(1, id);
        return sqlStatement;
    }


    protected abstract T getEntity(ResultSet resultSet) throws SQLException;

    protected abstract String getSqlForSingle();
}
