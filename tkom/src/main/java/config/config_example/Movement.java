package config.config_example;

import config.model.Config;

public class Movement implements Config {

  int minSpeed;
  int maxSpeed;
  String[] tags;
  Temperature[] temperatures;

  public int getMinSpeed() {
    return minSpeed;
  }

  public void setMinSpeed(int minSpeed) {
    this.minSpeed = minSpeed;
  }

  public int getMaxSpeed() {
    return maxSpeed;
  }

  public Temperature[] getTemperatures() {
    return temperatures;
  }

  public void setTemperatures(Temperature[] temperatures) {
    this.temperatures = temperatures;
  }

  public void setMaxSpeed(int maxSpeed) {
    this.maxSpeed = maxSpeed;
  }

  public String[] getTags() {
    return tags;
  }

  public void setTags(String[] tags) {
    this.tags = tags;
  }
}
