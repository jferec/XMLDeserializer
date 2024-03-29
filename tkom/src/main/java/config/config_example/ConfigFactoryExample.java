package config.config_example;

import config.model.Config;
import config.model.ConfigFactory;

public class ConfigFactoryExample implements ConfigFactory {

  @Override
  public Config getConfigInstanceByName(String className) {
    switch (className) {
      case "Machine":
        return new Machine();

      case "Temperature":
        return new Temperature();

      case "Movement":
        return new Movement();

      default:
        return null;
    }
  }

}
