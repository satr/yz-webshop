package io.github.satr.yzwebshop.repositories;

import io.github.satr.yzwebshop.entities.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProductRepository extends RepositoryBase<Product> {

    private final String MainSqlSelect = "SELECT Product.ID, Product.Name, Product.Price, COALESCE(Stock.Amount, 0) AS Amount FROM Product"
            + " LEFT OUTER JOIN Stock ON Stock.ProductID = Product.ID";

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
        return MainSqlSelect;
    }

    @Override
    protected String getSqlForSingle() {
        return MainSqlSelect + " WHERE ID = ?";
    }
}
