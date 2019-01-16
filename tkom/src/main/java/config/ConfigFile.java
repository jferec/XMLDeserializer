package config;

public class ConfigFile {

  private static String PROLOG = "<?xml version=\"1.0\"?>\n";
  private Field root;

  public ConfigFile(Field root) {
    this.root = root;
  }

  @Override
  public String toString() {
    return PROLOG + root;
  }

  public static Field root(String className) {
    return new ConfigObject(className);
  }

}
