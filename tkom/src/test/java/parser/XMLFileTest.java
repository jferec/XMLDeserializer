package parser;

import config.ConfigFieldImpl;
import config.ConfigFile;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class XMLFileTest {

  private static String PATH_ONE = "src/main/resources/field.xml";

  @Test
  public void testForPath() throws IOException {
    ConfigFile file = new ConfigFieldImpl("fieldOne", "alfa.gamma")
        .addChildField(new ConfigFieldImpl("fieldTwo", "alfa.delta")
            .addChildField(new ConfigFieldImpl("fieldFour", "alfa.alfa:attr1")))
        .addChildField(new ConfigFieldImpl("fieldThree", "alfa.fi")).build();
    FileUtils.writeStringToFile(new File(PATH_ONE), file.toString());
  }
}