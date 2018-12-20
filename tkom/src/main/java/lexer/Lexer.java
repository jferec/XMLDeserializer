package lexer;

import static com.google.common.base.Ascii.EOT;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Set;

public class Lexer {

  private static Set<Character> WHITE_CHARACTERS = Sets.newHashSet(' ', '\n', '\t', '\r');
  private Source src;
  private char next;

  public Lexer(String filePath) {
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

  private Token getNextToken() throws IOException {
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
        default:
          return new Token(TokenType.OpeningTagBegin);
      }
    } else if (first == '?' && second == '>') {
      popChar();
      return new Token(TokenType.PrologEnd);
    } else if (first == '/' && second == '>') {
      popChar();
      return new Token(TokenType.ClosingTagEnd);
    } else if (first == '>') {
      return new Token(TokenType.OpeningTagEnd);
    } else if (first == '=') {
      return new Token(TokenType.Equals);
    } else if (first == '"') {
      return new Token(TokenType.DoubleQuotationMark);
    } else if (first == '\'') {
      return new Token(TokenType.SingleQuotationMark);
    } else if (isWhiteCharacter(first)) {
      return new Token(TokenType.WhiteSpace);
    } else if (Character.isLetterOrDigit(first)) {
      return new Token(TokenType.Letter, first);
    } else {
      return new Token(TokenType.Unknown);
    }
  }

  private boolean hasNextChar() {
    return next != EOT;
  }

  private static boolean isWhiteCharacter(char c) {
    return WHITE_CHARACTERS.contains(c);
  }
}
