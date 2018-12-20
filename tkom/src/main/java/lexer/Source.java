package lexer;

import static com.google.common.base.Ascii.*;

import com.google.common.base.Ascii;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Source {
  private InputStream inputStream;

  public Source(String filePath) throws IOException {
    File inputFile = new File(filePath);
    this.inputStream = new FileInputStream(inputFile);
  }

  public char next() throws IOException {
    int next = inputStream.read();
    if(next == -1){
      return EOT;
    }
    return (char) next;
  }
}
