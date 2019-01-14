package config;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import org.junit.Test;

public class PathLexerTest {

  @Test
  public void testLexerForValid() throws ParseException {
    String path = "alfa.beta[1].gamma[2](3).delta[4]:attr1.fi:attr2";
    PathLexer lexer = new PathLexer(path).tokenize();
    PathToken token = lexer.getNextToken();
    assertEquals("alfa", token.getNodeName());
    assertEquals(0, token.getNodeIndex());
    assertEquals(null, token.getAttributeName());
    assertEquals(-1, token.getValueIndex());
    assertEquals(PathTokenType.Node, token.getType());
    token = lexer.getNextToken();
    assertEquals("beta", token.getNodeName());
    assertEquals(1, token.getNodeIndex());
    assertEquals(null, token.getAttributeName());
    assertEquals(-1, token.getValueIndex());
    assertEquals(PathTokenType.Node, token.getType());
    token = lexer.getNextToken();
    assertEquals("gamma", token.getNodeName());
    assertEquals(2, token.getNodeIndex());
    assertEquals(null, token.getAttributeName());
    assertEquals(3, token.getValueIndex());
    assertEquals(PathTokenType.Value, token.getType());
    token = lexer.getNextToken();
    assertEquals("delta", token.getNodeName());
    assertEquals(4, token.getNodeIndex());
    assertEquals("attr1", token.getAttributeName());
    assertEquals(-1, token.getValueIndex());
    assertEquals(PathTokenType.Attribute, token.getType());
    token = lexer.getNextToken();
    assertEquals("fi", token.getNodeName());
    assertEquals(0, token.getNodeIndex());
    assertEquals("attr2", token.getAttributeName());
    assertEquals(-1, token.getValueIndex());
    assertEquals(PathTokenType.Attribute, token.getType());
  }

}