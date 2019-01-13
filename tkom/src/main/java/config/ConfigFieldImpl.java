package config;

import java.util.ArrayList;

public class ConfigFieldImpl implements ConfigField {

  ArrayList<ConfigField> children = new ArrayList<>();
  final String name;
  final String path;

  public ConfigFieldImpl(String name, String path) {
    this.name = name;
    this.path = path;
  }

  @Override
  public ConfigField addChildField(ConfigField child) {
    children.add(child);
    return this;
  }

  @Override
  public ConfigFile build() {
    return new ConfigFile(this);
  }

  @Override
  public String toString() {
    if (children.isEmpty()) {
      return String.format("<%s path='%s'/>\n", name, path);
    } else {
      StringBuilder content = new StringBuilder();
      content.append(String.format("<%s path='%s'>\n", name, path));
      for (ConfigField child : children) {
        content
            .append('\t')
            .append(child.toString());
      }
      content.append(String.format("</%s>", name));
      return content.toString();
    }
  }
}
