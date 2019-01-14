package lexer;

import static com.google.common.base.Ascii.EOT;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Set;
import javax.management.modelmbean.XMLParseException;

public class Lexer {

  private static int MAX_SPECIAL_CHAR_LENGTH = 6;
  private static int MIN_SPECIAL_CHAR_LENGTH = 4;
  private static Set<Character> WHITE_CHARACTERS = Sets.newHashSet(' ', '\n', '\t', '\r');
  private static Set<Character> CHAR_SEQ_END = Sets.newHashSet('<', '>', '"', '\'', ':', '=', '/');
  private static ImmutableMap<String, Character> SPECIAL_CHARS = ImmutableMap.<String, Character>builder()
      .put("&amp;", '&')
      .put("&lt;", '<')
      .put("&gt;", '>')
      .put("&quot;", '"')
      .put("&apos", '\'')
      .build();

  private Source src;
  private char curr;
  private char next;

  public static Lexer of(String filePath) {
    return new Lexer(filePath);
  }

  private Lexer(String filePath) {
    try {
      this.src = new Source(filePath);
      this.curr = src.next();
      this.next = src.next();
    } catch (IOException e) {
      throw new RuntimeException("File under given path failed to load.");
    }
  }

  private String skipWhiteSpaces() throws IOException, XMLParseException {
    StringBuilder whiteSpace = new StringBuilder();
    while (curr != EOT && isWhiteCharacter(curr)) {
      whiteSpace.append(curr);
      popChar();
    }
    return whiteSpace.toString();
  }

  private Character popChar() throws IOException, XMLParseException {
    if (curr == EOT) {
      throw new XMLParseException(
          String.format("End of file was not expected here: %s", src.getPosition()));
    }
    curr = next;
    next = src.next();
    return curr;
  }

  private void popChar(int times) throws IOException, XMLParseException {
    for (int i = 0; i < times; i++) {
      popChar();
    }
  }

  public Token getNextToken() throws IOException, XMLParseException {
    if (!hasNextChar()) {
      return new Token(TokenType.EOT);
    }
    if (curr == '<') {
      switch (next) {
        case '?':
          popChar(2);
          return new Token(TokenType.PrologBegin);
        case '/':
          popChar(2);
          return new Token(TokenType.ClosingTagBegin);
        default:
          popChar();
          return new Token(TokenType.OpeningTagBegin);
      }
    } else if (curr == '?' && next == '>') {
      popChar(2);
      return new Token(TokenType.PrologEnd);
    } else if (curr == '/' && next == '>') {
      popChar(2);
      return new Token(TokenType.SelfClosingTag);
    } else if (curr == '>') {
      popChar();
      return new Token(TokenType.TagEnd);
    } else if (curr == '=') {
      popChar();
      return new Token(TokenType.Equals);
    } else if (curr == '\'' || curr == '"') {
      return new Token(TokenType.String,
          getString(curr));
    } else if (isWhiteCharacter(curr)) {
      return new Token(TokenType.WhiteSpace, skipWhiteSpaces());
    } else {
      return new Token(TokenType.CharSequence, getCharSequence());
    }
  }

  private String getString(char endChar)
      throws IOException, XMLParseException {
    StringBuilder sequence = new StringBuilder();
    while (next != endChar) {
      popChar();
      if (curr == '&') {
        sequence.append(processSpecialChar());
      } else {
        sequence.append(curr);
      }
    }
    popChar(2);
    return sequence.toString();
  }

  private String getCharSequence() throws IOException, XMLParseException {
    StringBuilder sequence = new StringBuilder();
    while (!doesEndCharSequence(curr)) {
      if (curr == '&') {
        sequence.append(processSpecialChar());
      } else {
        sequence.append(curr);
      }
      popChar();
    }
    return sequence.toString();
  }

  /*
    curr is '&'
   */
  private Character processSpecialChar() throws IOException, XMLParseException {
    StringBuilder specialChar = new StringBuilder();
    while (specialChar.length() < MAX_SPECIAL_CHAR_LENGTH) {
      specialChar.append(curr);
      if (specialChar.length() >= MIN_SPECIAL_CHAR_LENGTH && SPECIAL_CHARS
          .containsKey(specialChar.toString())) {
        return SPECIAL_CHARS.get(specialChar.toString());
      }
      popChar();
    }
    throw new XMLParseException(
        String.format("Failed to parse the special char at %s", src.getPosition()));
  }

  private boolean hasNextChar() {
    return curr != EOT;
  }

  private static boolean isWhiteCharacter(char c) {
    return WHITE_CHARACTERS.contains(c);
  }

  private boolean doesEndCharSequence(char c) {
    return WHITE_CHARACTERS.contains(c) || CHAR_SEQ_END.contains(c);
  }

  public String getPosition() {
    return src.getPosition();
  }

}
