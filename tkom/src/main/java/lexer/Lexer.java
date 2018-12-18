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
      src = new Source(filePath);
    } catch (IOException e) {
      throw new RuntimeException("File under given path failed to load.");
    }
  }

  public void skipWhiteSpaces() throws IOException {
    while (next != EOT && isWhiteCharacter(next)) {
      popNextChar();
    }
  }

  private char popNextChar() throws IOException {
    char curr = next;
    next = src.next();
    return curr;
  }

  private static boolean isWhiteCharacter(char c) {
    return WHITE_CHARACTERS.contains(c);
  }
}
