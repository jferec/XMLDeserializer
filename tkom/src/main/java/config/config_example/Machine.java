package config.config_example;

import config.Config;

public class Machine implements Config {

  private int height;
  private int weight;
  private String name;
  private Movement movement;
  private Temperature[] temperatures;

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Movement getMovement() {
    return movement;
  }

  public void setMovement(Movement movement) {
    this.movement = movement;
  }

  public Temperature[] getTemperatures() {
    return temperatures;
  }

  public void setTemperatures(Temperature[] temperatures) {
    this.temperatures = temperatures;
  }
}
