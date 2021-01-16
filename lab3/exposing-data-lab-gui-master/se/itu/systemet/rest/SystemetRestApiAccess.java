package se.itu.systemet.rest;

import java.util.List;
import java.util.ArrayList;
import se.itu.systemet.domain.Product;

public class SystemetRestApiAccess implements ApiAccess {


    public List<Product>fetch(Query query) {
      List<Product> products = new ArrayList<>();
      Client client = new Client("http://localhost:8080/search/products/all?" + query.toQueryString());
      String json = client.getJson();
      ProductParser parser = new ProductParser();
      products = parser.parse(json);
      System.out.println("Found: " + products.size() + " products.");
      if (products.size() < 40) {
        products.stream().forEach(System.out::println);
      }
      return products;
  }
}
