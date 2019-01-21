package config.model;

public class ConfigObject extends Field {

  public ConfigObject(String name) {
    super(name);
  }

  @Override
  public String toString() {
    StringBuilder content = new StringBuilder()
        .append(String.format("<%s type='object'>\n", name));
    for (Field child : children) {
      content.append(child.toString());
    }
    content.append(String.format("</%s>", name));
    return content.toString();
  }
}
