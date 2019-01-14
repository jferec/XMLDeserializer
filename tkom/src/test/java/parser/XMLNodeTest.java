package parser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import javax.management.modelmbean.XMLParseException;
import lexer.Lexer;
import org.junit.Test;

public class XMLNodeTest {

  @Test
  public void testMatchingEdge() {
    String edge = "alfa[5]";
    assertEquals(5, XMLNode.findIndex(edge, '[', ']'));
    assertEquals("alfa", XMLNode.findName(edge, '['));
  }

  @Test
  public void testMalformedEdge() {
    String edge = "afc[a]5";
    assertEquals(-1, XMLNode.findIndex(edge, '[', ']'));
    assertEquals("afc", XMLNode.findName(edge, '['));
  }

  @Test
  public void testForPath() throws IOException, XMLParseException, ParseException {
    Lexer lex = Lexer.of("src/main/resources/validxml4.xml");
    Parser parser = new Parser(lex);
    XMLFile xmlFile = parser.run();
    String abc = xmlFile.find("a.b.el[0]:attr");
    assertEquals("abc", abc);
    String raz = xmlFile.find("a.b[1].gamma(0)");
    assertEquals("raz", raz);
  }
}