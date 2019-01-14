package config.config_example;

import config.Config;
import config.ConfigFactory;

public class ConfigFactoryExample implements ConfigFactory {

  @Override
  public Config getConfigInstanceByName(String className) throws NoSuchFieldException {
    switch (className) {
      case "Machine":
        return new Machine();

      case "Temperature":
        return new Temperature();

      case "Movement":
        return new Movement();

      default:
        throw new NoSuchFieldException(
            String.format("Failed to find a constructor for %s", className));
    }
  }
}
