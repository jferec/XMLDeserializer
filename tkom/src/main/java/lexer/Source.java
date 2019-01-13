package lexer;

import static com.google.common.base.Ascii.EOT;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Source {

  private InputStream inputStream;
  private int line;
  private int position;

  public Source(String filePath) throws IOException {
    File inputFile = new File(filePath);
    this.inputStream = new FileInputStream(inputFile);
    this.line = 1;
    this.position = 0;
  }

  public char next() throws IOException {
    int next = inputStream.read();
    if (next == -1) {
      return EOT;
    }
    if (next == '\n') {
      line++;
      position = 0;
    }
    position++;
    return (char) next;
  }

  public String getPosition() {
    return String.format("%s:%s", line, position);
  }
}
