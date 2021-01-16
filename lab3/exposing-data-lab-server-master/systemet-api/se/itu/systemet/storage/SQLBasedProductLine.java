package se.itu.systemet.storage;

import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner.*;
import java.sql.*;

import se.itu.systemet.domain.Product;



/**
 * <p>An implementation of ProuctLine which reads products from the database.
 * </p>
 */
public class SQLBasedProductLine implements ProductLine {

  private List<Product> products;

  // Prevent instantiation from outside this package
  SQLBasedProductLine() { }

  public List<Product> getProductsFilteredBy(Predicate<Product> predicate) {
    if (products == null) {
      readProductsFromDatabase();
    }
    return products.stream().filter(predicate).collect(Collectors.toList());
  }

  public List<Product> getAllProducts() {
    if (products == null) {
      readProductsFromDatabase();
    }
    return products;
  }

  private Product getFakeProduct(String name, String price,
  String alcohol,
  String volume,
  String nr,
  String productGroup,
  String type) {

    Product product = new Product.Builder()
    .name(name)
    .price(Double.parseDouble(price))
    .alcohol(Double.parseDouble(alcohol))
    .volume(Integer.parseInt(volume))
    .nr(Integer.parseInt(nr))
    .productGroup(productGroup)
    .type(type)
    .build();

    return product;
  }

  private void readProductsFromDatabase() {//tar in resultatet av en sql sats och letar efter nycklarna.
      products = new ArrayList<>();
      ResultSet rs = DBHelper.productsResultSet();
      try {
        while (rs.next()) {
          Product p = new Product.Builder()
          .name(rs.getString(DBHelper.ColumnId.NAME))//DBHelper.PRODUCT_TABLE.PRODUCT_NAME
          .nr(rs.getInt(DBHelper.ColumnId.PRODUCT_NR))
          .alcohol(rs.getDouble(DBHelper.ColumnId.ALCOHOL))
          .price(rs.getDouble(DBHelper.ColumnId.PRICE))//"price
          .volume(rs.getInt(DBHelper.ColumnId.VOLUME))
          .type(rs.getString(DBHelper.ColumnId.TYPE))
          .productGroup(rs.getString(DBHelper.ColumnId.PRODUCT_GROUP))//PRODUCT_GROUP_ID???
          .build();
          products.add(p);
        }
      } catch (SQLException e) {
        System.err.println("Error getting products from DB: " + e.getMessage());
      } finally {
        try {
          rs.close();
        } catch (SQLException close) {
          System.err.println(close.getMessage());
        }
    }
  }
}
