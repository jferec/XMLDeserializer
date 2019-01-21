package config.model;

public class ConfigArray extends Field {

  public ConfigArray(String name) {
    super(name);
  }

  @Override
  public String toString() {
    StringBuilder content = new StringBuilder()
        .append(String.format("<%s type='array'>\n", name));
    for (Field child : children) {
      content.append("<element>")
          .append('\t')
          .append(child.toString())
          .append("</element>\n");
    }
    content.append(String.format("</%s>", name));
    return content.toString();
  }
}
