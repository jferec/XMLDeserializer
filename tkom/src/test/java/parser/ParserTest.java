package parser;

import static org.junit.Assert.*;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.management.modelmbean.XMLParseException;
import lexer.Lexer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class ParserTest {
  @Test
  public void parsePrologOnly() throws IOException, XMLParseException {
    Lexer lexer = Lexer.of("src/main/resources/validxml.xml");
    Parser parser = new Parser(lexer);
    XMLFile result = parser.run();
    Assert.assertNull(result.getRoot());
  }

  @Test
  public void XMLWithValuesAndAttributes() throws IOException, XMLParseException {
    Lexer lexer = Lexer.of("src/main/resources/validxml3.xml");
    Parser parser = new Parser(lexer);
    XMLFile result = parser.run();
    Map<String, XMLNode> rootChildren = result.getRoot().getChildren();
    Assert.assertEquals(Sets.newHashSet("beta", "gamma"), rootChildren.keySet());
    XMLNode beta = rootChildren.get("beta");
    XMLNode gamma = rootChildren.get("gamma");

    Map<String, XMLNode> betaChildren = beta.getChildren();
    Map<String, XMLNode> gammaChildren = gamma.getChildren();
    Map<String, XMLAttribute> betaAttributes = beta.getAttributes();
    Assert.assertEquals(2, betaAttributes.size());
    Assert.assertEquals(0, gamma.getAttributes().size());
    Set<String> betaAttributeNames = new HashSet<>();
    for(XMLAttribute a : betaAttributes.values()){
      betaAttributeNames.add(a.getName());
    }
    Assert.assertEquals(Sets.newHashSet("attr1", "attr2"), betaAttributeNames);
    Assert.assertEquals(Sets.newHashSet(), betaChildren.keySet());
    Assert.assertEquals(Sets.newHashSet("halko", "delta"), gammaChildren.keySet());
    Assert.assertEquals("8val", beta.getValue());
    Assert.assertNull(gamma.getValue());
  }

  @Test
  public void tagNotClosed() throws IOException, XMLParseException {
    Lexer lexer = Lexer.of("src/main/resources/invalidxml.xml");
    Parser parser = new Parser(lexer);
    Assertions.assertThrows(XMLParseException.class, parser::run);
  }

  @Test
  public void missingProlog() throws IOException, XMLParseException{
    Lexer lexer = Lexer.of("src/main/resources/invalidxml2.xml");
    Parser parser = new Parser(lexer);
    Assertions.assertThrows(XMLParseException.class, parser::run);
  }

  @Test
  public void tagNameStartsWithSpace() throws IOException, XMLParseException{
    Lexer lexer = Lexer.of("src/main/resources/invalidxml3.xml");
    Parser parser = new Parser(lexer);
    Assertions.assertThrows(XMLParseException.class, parser::run);
  }

  @Test
  public void openingTagAfterClosingRoot() throws IOException, XMLParseException{
    Lexer lexer = Lexer.of("src/main/resources/invalidxml4.xml");
    Parser parser = new Parser(lexer);
    Assertions.assertThrows(XMLParseException.class, parser::run);
  }
}
