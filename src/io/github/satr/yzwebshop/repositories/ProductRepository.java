package io.github.satr.yzwebshop.repositories;

import io.github.satr.yzwebshop.entities.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProductRepository extends RepositoryBase<Product> {

    @Override
    protected void addEntity(List<Product> list, ResultSet resultSet) throws SQLException {
        list.add(getEntity(resultSet));
    }

    @Override
    protected Product getEntity(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getInt("ID"));
        product.setName(resultSet.getString("Name"));
        product.setPrice(resultSet.getFloat("Price"));
        product.setAmount(resultSet.getInt("Amount"));
        return product;
    }

    @Override
    protected String getSqlForList() {
        return "SELECT Product.ID, Product.Name, Product.Price, COALESCE(Stock.Amount, 0) AS Amount FROM Product"
                + " LEFT OUTER JOIN Stock ON Stock.ProductID = Product.ID";
    }

    @Override
    protected String getSqlForSingle() {
        return String.format("%s WHERE ID = ?", getSqlForList());
    }

    @Override
    protected void executeUpdate(Connection connection, Product entity) throws SQLException {
        if(entity.getId() == 0)
            addProduct(connection, entity);
        else
            updateProduct(connection, entity);
    }

    private void addProduct(Connection connection, Product entity) throws SQLException {
        /*
        CREATE TABLE Product
        (
            ID INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
            Name VARCHAR(500),
            Price FLOAT
        );
        CREATE TABLE Stock
        (
            ProductID INT(11) NOT NULL,
            Amount INT(11) NOT NULL,
            CONSTRAINT FK_Stock_Product FOREIGN KEY (ProductID) REFERENCES Product (ID)
        );
        CREATE INDEX FK_Stock_Product ON Stock (ProductID);
        */
        String sql = "INSERT INTO Product (Name, Price) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, entity.getName());
        preparedStatement.setDouble(2, entity.getPrice());
        preparedStatement.executeUpdate();
        //New row in the table Stock is created by a trigger:
        /*
        CREATE TRIGGER Product_Stock_Insert
        AFTER INSERT ON Product
        FOR EACH ROW BEGIN
        INSERT INTO Stock (ProductID, Amount)
        VALUES (NEW.ID, 0);
        END
        */
    }

    private void updateProduct(Connection connection, Product entity) throws SQLException {
        String sql = "UPDATE Product SET Name = ?, Price = ? WHERE ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, entity.getName());
        preparedStatement.setDouble(2, entity.getPrice());
        preparedStatement.setInt(3, entity.getId());
        preparedStatement.executeUpdate();

        updateAmount(connection, entity);
    }

    private void updateAmount(Connection connection, Product entity) throws SQLException {
        PreparedStatement preparedStatement;
        String sql = "UPDATE Stock SET Amount = ? WHERE ProductID = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, entity.getAmount());
        preparedStatement.setInt(2, entity.getId());
        preparedStatement.executeUpdate();
    }
}
