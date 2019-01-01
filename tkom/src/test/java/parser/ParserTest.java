package parser;

import static org.junit.Assert.*;

import java.io.IOException;
import javax.management.modelmbean.XMLParseException;
import lexer.Lexer;
import org.junit.Assert;
import org.junit.Test;

public class ParserTest {
  @Test
  public void parsePrologOnly() throws IOException, XMLParseException {
    Lexer lexer = Lexer.of("src/main/resources/validxml.xml");
    Parser parser = new Parser(lexer);
    XMLFile result = parser.run();
    Assert.assertNull(result.getRoot());
  }

  @Test
  public void simpleXMLWithValuesAndAttributes() throws IOException, XMLParseException {
    Lexer lexer = Lexer.of("src/main/resources/validxml3.xml");
    Parser parser = new Parser(lexer);
    XMLFile result = parser.run();
    System.out.println("halo");
  }
}