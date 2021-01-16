package servlets;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Predicate;
import javax.servlet.*;
import javax.servlet.http.*;
import static java.nio.charset.StandardCharsets.UTF_8;

import se.itu.systemet.domain.Product;
import se.itu.systemet.storage.ProductLine;
import se.itu.systemet.storage.ProductLineFactory;
import java.util.Locale;
import static java.util.Locale.US;

public class SystemetWebAPI extends HttpServlet {

  @Override
  public void init() throws ServletException {
    System.setProperty("ProductLine",//DB?!
                       getServletContext().getInitParameter("ProductLine"));//DB?!
    Locale.setDefault(US);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    request.setCharacterEncoding(UTF_8.name());
    response.setContentType("application/json;charset=" + UTF_8.name());
    response.setCharacterEncoding(UTF_8.name());
    PrintWriter out = response.getWriter();

    /*System.out.println("Got query from: " +
                       request.getRemoteAddr() +
                       "/" + request.getRemoteHost() +
                       " using " +
                       request.getHeader("user-agent"));
*/
    ParameterParser paramParser =
      new ParameterParser(request.getQueryString().split("&"));

    List<String> invalids = paramParser.invalidArgs();
    
    invalids.removeIf(s -> s.endsWith("=") || "".equals(""));
    if (invalids.size() > 0) {
      out.println("{ \"error\" : \"invaldid args" + invalids + "\"}");
      out.close();
      //System.out.println(invalids);
      return;
    }
    Predicate<Product> filter = paramParser.filter();

    ProductLine productLine = ProductLineFactory.getProductLine();

    List<Product> products = productLine.getProductsFilteredBy(filter);

    Formatter formatter = FormatterFactory.getFormatter();
    String json = formatter.format(products);

    StringBuilder sb = new StringBuilder(json);
    out.println(sb.toString());
    out.close();
  }
}
