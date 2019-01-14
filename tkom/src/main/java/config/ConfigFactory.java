package config;

public interface ConfigFactory {

  /**
   * Returns an instance of the object with the given class name
   *
   * @param className - name of the class implementing Config interface
   * @return - instance of the className
   */
  public Config getConfigInstanceByName(String className) throws NoSuchFieldException;
}
