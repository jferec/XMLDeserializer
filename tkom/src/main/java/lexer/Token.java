package lexer;

public class Token {

  private TokenType type;
  private Character value;

  public Token(TokenType t, Character v) {
    if (t == null) {
      throw new IllegalArgumentException("TokenType is absent.");
    }
    if (t.equals(TokenType.Letter) && v == null) {
      throw new IllegalArgumentException("Letter Token must have a value");
    }
    if (!t.equals(TokenType.Letter) && v != null) {
      throw new IllegalArgumentException("Token other than Letter must not have any value");
    }
    this.type = t;
    this.value = v;
  }

  public Token(TokenType t){
    this(t, null);
  }

  public TokenType getType() {
    return type;
  }

  public Character getValue() {
    return value;
  }
}
