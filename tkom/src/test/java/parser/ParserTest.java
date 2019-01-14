package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import javax.management.modelmbean.XMLParseException;
import lexer.Lexer;
import org.junit.Test;

public class ParserTest {

  @Test
  public void parsePrologOnly() throws IOException, XMLParseException {
    Lexer lexer = Lexer.of("src/main/resources/validxml.xml");
    Parser parser = new Parser(lexer);
    parser.run();
  }

  @Test
  public void XMLWithValuesAndAttributes() throws IOException, XMLParseException {
    Lexer lexer = Lexer.of("src/main/resources/validxml3.xml");
    Parser parser = new Parser(lexer);
    XMLFile result = parser.run();
    assertNotNull(result);
    XMLNode root = result.getRoot();
    assertEquals(1, root.getChildren().size());
    ArrayList<XMLNode> alfaNodes = root.getChildren().get("alfa");
    assertEquals(1, alfaNodes.size());
    XMLNode xmlNode = alfaNodes.get(0);
    assertEquals(2, xmlNode.getChildren().size());
    ArrayList<XMLNode> betaNodes = xmlNode.getChildren().get("beta");
    ArrayList<XMLNode> gammaNodes = xmlNode.getChildren().get("gamma");
    assertEquals(1, betaNodes.size());
    assertEquals(1, gammaNodes.size());
    XMLNode beta = betaNodes.get(0);
    assertEquals(2, beta.getAttributes().size());
    assertEquals("abc", beta.getAttributes().get("attr1").getValue());
    assertEquals("def", beta.getAttributes().get("attr2").getValue());
    XMLNode gamma = gammaNodes.get(0);
    assertEquals(3, gamma.getValues().size());
    assertEquals("dwa", gamma.getValues().get(1));
    assertEquals("trzy", gamma.getValues().get(2));
    assertEquals(2, gamma.getChildren().size());
    XMLNode delta = gamma.getChildren().get("delta").get(0);
    assertTrue(delta.getValues().isEmpty());
    assertEquals("val3", delta.getAttributes().get("attr2").getValue());
    XMLNode halko = gamma.getChildren().get("halko").get(0);
    assertEquals("val4", halko.getAttributes().get("attr4").getValue());
    assertTrue(halko.getValues().isEmpty());
  }

  @Test
  public void tagNotClosed() throws IOException, XMLParseException {
    Lexer lexer = Lexer.of("src/main/resources/invalidxml.xml");
    Parser parser = new Parser(lexer);
    assertThrows(XMLParseException.class, parser::run);
  }

  @Test
  public void missingProlog() throws IOException, XMLParseException {
    Lexer lexer = Lexer.of("src/main/resources/invalidxml2.xml");
    Parser parser = new Parser(lexer);
    assertThrows(XMLParseException.class, parser::run);
  }

  @Test
  public void tagNameStartsWithSpace() throws IOException, XMLParseException {
    Lexer lexer = Lexer.of("src/main/resources/invalidxml3.xml");
    Parser parser = new Parser(lexer);
    assertThrows(XMLParseException.class, parser::run);
  }

  @Test
  public void openingTagAfterClosingRoot() throws IOException, XMLParseException {
    Lexer lexer = Lexer.of("src/main/resources/invalidxml4.xml");
    Parser parser = new Parser(lexer);
    assertThrows(XMLParseException.class, parser::run);
  }
}
