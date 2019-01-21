package config.model;

import java.util.ArrayList;

public abstract class Field {

  protected ArrayList<Field> children = new ArrayList<>();
  protected String name;

  protected Field(String name) {
    this.name = name;
  }

  public Field addChild(Field child) throws NoSuchMethodException {
    children.add(child);
    return this;
  }

  public Field addObject(Field object) {
    children.add(object);
    return this;
  }

  public Field addArray(Field array) {
    children.add(array);
    return this;
  }

  public Field setAttribute(String name, String path) {
    children.add(new ConfigValue(name, path));
    return this;
  }

  public ConfigFile build() {
    return new ConfigFile(this);
  }

}
