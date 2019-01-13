package lexer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import javax.management.modelmbean.XMLParseException;
import org.junit.Test;

public class LexerTest {

  private Token next;

  @Test
  public void testValidXML() throws IOException, XMLParseException {
    Lexer lexer = Lexer.of("src/main/resources/validxml.xml");
    assertEquals(TokenType.PrologBegin, lexer.getNextToken().getType());
    next = lexer.getNextToken();
    assertEquals(TokenType.CharSequence, next.getType());
    assertEquals("xml", next.getValue());
    assertEquals(TokenType.WhiteSpace, lexer.getNextToken().getType());
    next = lexer.getNextToken();
    assertEquals(TokenType.CharSequence, next.getType());
    assertEquals("version", next.getValue());
    assertEquals(TokenType.WhiteSpace, lexer.getNextToken().getType());
    assertEquals(TokenType.Equals, lexer.getNextToken().getType());
    assertEquals(TokenType.WhiteSpace, lexer.getNextToken().getType());
    next = lexer.getNextToken();
    assertEquals(TokenType.String, next.getType());
    assertEquals("1.0&", next.getValue());
    assertEquals(TokenType.WhiteSpace, lexer.getNextToken().getType());
    assertEquals(TokenType.PrologEnd, lexer.getNextToken().getType());
  }

  @Test
  public void testValidXMLTagsWithValue() throws IOException, XMLParseException {
    Lexer lexer = Lexer.of("src/main/resources/validxml2.xml");
    assertEquals(TokenType.OpeningTagBegin, lexer.getNextToken().getType());
    next = lexer.getNextToken();
    assertEquals("message", next.getValue());
    assertEquals(TokenType.CharSequence, next.getType());
    assertEquals(TokenType.TagEnd, lexer.getNextToken().getType());
    next = lexer.getNextToken();
    assertEquals(TokenType.CharSequence, next.getType());
    assertEquals("salary", next.getValue());
    assertEquals(TokenType.WhiteSpace, lexer.getNextToken().getType());
    next = lexer.getNextToken();
    assertEquals(TokenType.CharSequence, next.getType());
    assertEquals("<", next.getValue());
  }
}