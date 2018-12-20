package lexer;

import static org.junit.Assert.*;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

public class LexerTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void a(){
    try {
      Source src = new Source("halo");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}