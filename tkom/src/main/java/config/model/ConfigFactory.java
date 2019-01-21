package config.model;

import java.text.ParseException;

public interface ConfigFactory {

  /**
   * Returns an instance of the object with the given class name
   *
   * @param className - name of the class implementing Config interface
   * @return - instance of the className
   */
  Config getConfigInstanceByName(String className) throws NoSuchFieldException;

  static Object mapType(String typeName, String value) throws ParseException {
    switch (typeName.toLowerCase()) {
      case "int":
      case "integer":
        return Integer.valueOf(value);
      case "long":
        return Long.valueOf(value);
      case "boolean":
        return Boolean.valueOf(value);
      case "float":
        return Float.valueOf(value);
      case "double":
        return Double.valueOf(value);
      case "string":
        return value;
      case "char":
      case "character":
        if (value.length() == 1) {
          return value.charAt(0);
        }
    }
    return null;
  }
}

