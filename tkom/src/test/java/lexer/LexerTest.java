package lexer;

import static org.junit.Assert.*;

import java.io.IOException;
import org.junit.Test;

public class LexerTest {

  @Test
  public void testValidXML() throws IOException {
    Lexer lexer = new Lexer("src/main/resources/validxml.xml");
    assertEquals(TokenType.PrologBegin, lexer.getNextToken().getType());
    String xml = "xml";
    for (int i = 0; i < xml.length(); i++) {
      Token curr = lexer.getNextToken();
      assertEquals(TokenType.Char, curr.getType());
      assertEquals((Character) xml.charAt(i), curr.getValue());
    }
    assertEquals(TokenType.WhiteSpace, lexer.getNextToken().getType());
    String version = "version";
    checkString(lexer, version);
    assertEquals(TokenType.Equals, lexer.getNextToken().getType());
    assertEquals(TokenType.DoubleQuotationMark, lexer.getNextToken().getType());
    checkString(lexer, "1.0");
    assertEquals(TokenType.DoubleQuotationMark, lexer.getNextToken().getType());
    assertEquals(TokenType.PrologEnd, lexer.getNextToken().getType());
    assertNull(lexer.getNextToken());
  }

  @Test
  public void testValidXMLTagsWithValue() throws IOException {
    Lexer lexer = new Lexer("src/main/resources/validxml2.xml");
    assertEquals(TokenType.OpeningTagBegin, lexer.getNextToken().getType());
    checkString(lexer, "message");
    assertEquals(TokenType.TagEnd, lexer.getNextToken().getType());
    checkString(lexer, "Remember me this weekend");
    assertEquals(TokenType.ClosingTagBegin, lexer.getNextToken().getType());
    checkString(lexer, "message");
    assertEquals(TokenType.TagEnd, lexer.getNextToken().getType());
    assertEquals(TokenType.WhiteSpace, lexer.getNextToken().getType());
    assertEquals(TokenType.OpeningTagBegin, lexer.getNextToken().getType());
    checkString(lexer, "raz");
    assertEquals(TokenType.SelfClosingTag, lexer.getNextToken().getType());
    assertNull(lexer.getNextToken());
  }

  private static void checkString(Lexer lexer, String expected) throws IOException {
    int len = expected.length();
    for (int i = 0; i < len; i++) {
      Token curr = lexer.getNextToken();
      if (curr.getType().equals(TokenType.WhiteSpace)) {
        assertTrue(Lexer.isWhiteCharacter(expected.charAt(i)));
      } else {
        assertEquals(TokenType.Char, curr.getType());
        assertEquals((Character) expected.charAt(i), curr.getValue());
      }
    }
  }
}