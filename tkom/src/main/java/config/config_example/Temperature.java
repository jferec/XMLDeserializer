package config.config_example;

import config.Config;

public class Temperature implements Config {

  int min;
  int max;
  float current;


  public int getMin() {
    return min;
  }

  public void setMin(int min) {
    this.min = min;
  }

  public int getMax() {
    return max;
  }

  public void setMax(int max) {
    this.max = max;
  }

  public float getCurrent() {
    return current;
  }

  public void setCurrent(float current) {
    this.current = current;
  }
}
