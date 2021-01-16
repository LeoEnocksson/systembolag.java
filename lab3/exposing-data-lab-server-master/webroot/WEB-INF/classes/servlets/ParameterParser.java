package servlets;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.Arrays;
import java.util.stream.Collectors;

import se.itu.systemet.domain.Product;

public class ParameterParser {
  private String[] args;
  public ParameterParser(String[] args) {
    this.args = args;
  }
  /*
  private String getName() {
    String name = null;
    for (String arg : args) {
      if (arg.startsWith("name=")) {
        try {
          name = decode(arg.split("=") [1], UTF_8.name());
          break;
        } catch (Exception e) {}
      }
    }
    return name;
  }
*/
  public Predicate<Product> filter() {
    List<Predicate<Product>> predicates = parse();
    /*
    String name = getName();
    if (name != null) {
      predicates.add(p -> p.name().contains(name));
    }
    */
     // get a list of predicates
    // Reduce the list of predicates using "and"
    // https://docs.oracle.com/javase/tutorial/collections/streams/reduction.html
    return predicates.stream().reduce(p -> true, Predicate::and);
  }

  private static boolean isDouble(String value) {
    try {
      Double.parseDouble(value);
      return true;
    } catch (NumberFormatException nfe) {
      return false;
    }
  }

  private boolean isValidKey(String key) {
    return key.split("=").length==2 &&
      Arrays.stream(new String[] {
          "min_price",
          "max_price",
          "min_alcohol",
          "max_alcohol",
          "name"
        }).collect(Collectors.toList()).contains(key.split("=")[0]);
  }

  private List<Predicate<Product>> parse() {
    List<Predicate<Product>> predicates = new ArrayList<>();
    List<String> validArgs = new ArrayList<>(Arrays.asList(args));
    //validArgs.removeIf(s->!isValidKey(s) || !isDouble(s.split("=")[1]));
    //validArgs.removeIf(s->!isValidKey(s));// || !isDouble(s.split("=")[1]));
    validArgs.removeIf(s->invalidArgs().contains(s));
    for (String arg : validArgs) {
      String value = arg.split("=")[1];
      switch(arg.split("=")[0]) { // Check what filter it is
        case "max_price": predicates.add(p -> p.price() <= Double.parseDouble(value));
          break;
        case "min_price": predicates.add(p -> p.price() >= Double.parseDouble(value));
          break;
        case "min_alcohol": predicates.add(p -> p.alcohol() >= Double.parseDouble(value));
          break;
        case "max_alcohol": predicates.add(p -> p.alcohol() <= Double.parseDouble(value));
          break;
        case "name": predicates.add(p -> p.name().contains(value));
          break;
        default:
          continue;
      }
    }
    return predicates;
  }

  public List<String> invalidArgs() {
    List<String> invalids = Arrays.stream(args)
      .filter(a->!isValidKey(a) || (!isDouble(a.split("=")[1]) &&
      !a.split("=") [0].equals("name")))
      .collect(Collectors.toList());
      //System.out.println(invalids);
    return invalids;
  }
}
