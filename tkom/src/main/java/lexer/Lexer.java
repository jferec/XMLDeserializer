package lexer;

import static com.google.common.base.Ascii.EOT;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Set;

public class Lexer {

  private static Set<Character> WHITE_CHARACTERS = Sets.newHashSet(' ', '\n', '\t', '\r');
  private Source src;
  private char next;

  public static Lexer of(String filePath){
    return new Lexer(filePath);
  }

  private Lexer(String filePath) {
    try {
      this.src = new Source(filePath);
      this.next = src.next();
    } catch (IOException e) {
      throw new RuntimeException("File under given path failed to load.");
    }
  }

  public void skipWhiteSpaces() throws IOException {
    while (next != EOT && isWhiteCharacter(next)) {
      popChar();
    }
  }

  private char popChar() throws IOException {
    char curr = next;
    next = src.next();
    return curr;
  }

  public Token getNextToken() throws IOException {
    if (!hasNextChar()) {
      return null;
    }
    char first = popChar();
    char second = next;
    if (first == '<') {
      switch (second) {
        case '?':
          popChar();
          return new Token(TokenType.PrologBegin);
        case '/':
          popChar();
          return new Token(TokenType.ClosingTagBegin);
        case '-':
          popChar();
        default:
          return new Token(TokenType.OpeningTagBegin);
      }
    } else if (first == '?' && second == '>') {
      popChar();
      return new Token(TokenType.PrologEnd);
    } else if (first == '/' && second == '>') {
      popChar();
      return new Token(TokenType.SelfClosingTag);
    } else if (first == '>') {
      return new Token(TokenType.TagEnd);
    } else if (first == '=') {
      return new Token(TokenType.Equals);
    } else if (first == '"') {
      return new Token(TokenType.DoubleQuotationMark);
    } else if (first == '\'') {
      return new Token(TokenType.SingleQuotationMark);
    } else if (isWhiteCharacter(first)) {
      return new Token(TokenType.WhiteSpace);
    } else {
      return new Token(TokenType.Char, first);
    }
  }

  private boolean hasNextChar() {
    return next != EOT;
  }

  protected static boolean isWhiteCharacter(char c) {
    return WHITE_CHARACTERS.contains(c);
  }
}
