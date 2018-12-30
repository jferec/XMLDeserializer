package parser;

public class XMLAttribute {

  private final String name;
  private final String value;

  XMLAttribute(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }
}
