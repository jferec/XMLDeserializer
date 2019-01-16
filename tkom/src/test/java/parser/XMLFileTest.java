package parser;

import config.ConfigArray;
import config.ConfigFile;
import config.ConfigObject;
import config.ConfigValue;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class XMLFileTest {

  private static String PATH_ONE = "src/main/resources/examplemapping.xml";

  @Test
  public void testForPath() throws IOException, NoSuchMethodException {
    ConfigFile file = ConfigFile.root("Machine")
        .setAttribute("name", "alfa.beta[0].gamma:attr1")
        .setAttribute("height", "alfa(0)")
        .addObject(new ConfigObject("movement")
            .setAttribute("minSpeed", "alfa.beta[1](0)")
            .setAttribute("maxSpeed", "alfa(1)")
            .addArray(new ConfigArray("tags")
                .addChild(new ConfigValue("element", "alfa.beta[0].gamma:attr1"))
                .addChild(new ConfigValue("element", "alfa.beta[2].fi(0)")))
            .addArray(new ConfigArray("temperatures")
                .addObject(new ConfigObject("temperature")
                    .setAttribute("min", "alfa[0](0)")
                    .setAttribute("max", "alfa[0](2)")
                    .setAttribute("current", "alfa.beta[0](0)"))
                .addObject(new ConfigObject("temp")
                    .setAttribute("min", "alfa[0](0)")
                    .setAttribute("max", "alfa[0](0)")
                    .setAttribute("current", "alfa.beta[0](0)"))))
        .build();
    FileUtils.writeStringToFile(new File(PATH_ONE), file.toString());
  }
}