package config.config_example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import config.ConfigMaker;
import java.lang.reflect.Field;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

public class ConfigFactoryExampleTest {

  @Test
  public void runConfigMaker()
      throws Exception {
    ConfigFactoryExample configFactory = new ConfigFactoryExample();
    String mappingFilePath = "src/main/resources/examplemapping.xml";
    String inputFilePath = "src/main/resources/exampleinput.xml";
    ConfigMaker maker = new ConfigMaker(inputFilePath, mappingFilePath, new ConfigFactoryExample());
    Object config = maker.getConfig();
    assertTrue(config instanceof Machine);
    Machine machine = (Machine) config;
    assertEquals(20, machine.getHeight());
    assertEquals("name", machine.getName());
    String[] strings;
    Field array = FieldUtils.getField(Movement.class, "tags", true);

    System.out.println(array.getType().getSimpleName());
    ;

  }

}