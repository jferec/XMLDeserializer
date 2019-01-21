package config.config_example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import config.ConfigMaker;
import config.model.ConfigArray;
import config.model.ConfigFile;
import config.model.ConfigObject;
import config.model.ConfigValue;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ConfigMakerExampleTest {

  private static String MAPPING_FILE_PATH = "src/main/resources/examplemapping.xml";
  private static String INPUT_FILE_PATH = "src/main/resources/exampleinput.xml";

  @Test
  public void runConfigMaker()
      throws Exception {
    prepareMappingFile();
    ConfigMaker maker = new ConfigMaker(INPUT_FILE_PATH, MAPPING_FILE_PATH,
        new ConfigFactoryExample());
    Object config = maker.getConfig();
    assertTrue(config instanceof Machine);
    Machine machine = (Machine) config;
    assertEquals(20, machine.getHeight());
    assertEquals("name", machine.getName());
    assertNotNull(machine.getMovement());
    Movement movement = machine.getMovement();
    assertEquals(422, movement.getMinSpeed());
    assertEquals(456, movement.getMaxSpeed());
    String[] tags = movement.getTags();
    Temperature[] temperatures = movement.getTemperatures();
    assertEquals(2, temperatures.length);
    assertEquals(2, tags.length);
    assertEquals("name", tags[0]);
    assertEquals("fivalue", tags[1]);
    Temperature one = temperatures[0];
    Temperature two = temperatures[1];
    assertEquals(20, one.getMin());
    assertEquals(40, one.getMax());
    assertEquals(2.0, one.getCurrent(), 0.01f);
    assertEquals(20, two.getMin());
    assertEquals(20, two.getMax());
    assertEquals(2.0, two.getCurrent(), 0.01f);
  }


  private void prepareMappingFile() throws IOException, NoSuchMethodException {
    ConfigFile file = ConfigFile.root("Machine")
        .setAttribute("name", "alfa.beta[0].gamma:attr1")
        .setAttribute("height", "alfa(0)")
        .setChild(new ConfigObject("movement")
            .setAttribute("minSpeed", "alfa.beta[1](0)")
            .setAttribute("maxSpeed", "alfa(1)")
            .setChild(new ConfigArray("tags")
                .setChild(new ConfigValue("element", "alfa.beta[0].gamma:attr1"))
                .setChild(new ConfigValue("element", "alfa.beta[1].fi(0)")))
            .setChild(new ConfigArray("temperatures")
                .setChild(new ConfigObject("temperature")
                    .setAttribute("min", "alfa[0](0)")
                    .setAttribute("max", "alfa[0](2)")
                    .setAttribute("current", "alfa.beta[0](0)"))
                .setChild(new ConfigObject("temp")
                    .setAttribute("min", "alfa[0](0)")
                    .setAttribute("max", "alfa[0](0)")
                    .setAttribute("current", "alfa.beta[0](0)"))))
        .build();
    FileUtils.writeStringToFile(new File(MAPPING_FILE_PATH), file.toString());
  }
}

