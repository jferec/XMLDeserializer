package lexer;

import javax.management.modelmbean.XMLParseException;

public class Token {

  private TokenType type;
  private String value;

  public Token(TokenType t, String v) throws XMLParseException {
    if ((!t.equals(TokenType.WhiteSpace) && !t.equals(TokenType.CharSequence) && !t
        .equals(TokenType.String))) {
      throw new XMLParseException(
          String.format("Token of type %s, should have value assigned", t.name()));
    }
    this.type = t;
    this.value = v;
  }

  public Token(TokenType t) {

    this.type = t;
    this.value = null;
  }

  public TokenType getType() {
    return type;
  }

  public String getValue() {
    return value;
  }
}
