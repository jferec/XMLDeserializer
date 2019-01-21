package config.model;

public class ConfigValue extends Field {

  private String path;

  public ConfigValue(String name, String path) {
    super(name);
    this.path = path;
  }

  @Override
  public Field addChild(Field field) throws NoSuchMethodException {
    throw new NoSuchMethodException("Adding children to ConfigValue object is illegal");
  }

  @Override
  public String toString() {
    return String.format("<%s type='value' path='%s'/>\n", name, path);
  }
}
