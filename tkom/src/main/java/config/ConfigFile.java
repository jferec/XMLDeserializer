package config;

public class ConfigFile {

  private static String PROLOG = "<?xml version=\"1.0\"?>\n";
  private ConfigField root;

  public ConfigFile(ConfigField root) {
    this.root = root;
  }

  @Override
  public String toString() {
    return PROLOG + root;
  }
}
