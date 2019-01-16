package config.config_example;

import config.Config;

public class Machine implements Config {

  private int height;
  private String name;
  private Movement movement;

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}

