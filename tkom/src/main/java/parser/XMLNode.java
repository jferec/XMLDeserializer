package parser;

import java.util.HashMap;
import java.util.Map;

public class XMLNode {

  private final String name;
  private final String type;
  private final String value;
  private Map<String, XMLNode> children = new HashMap<>();
  private Map<String, XMLAttribute> attributes = new HashMap<>();

  XMLNode(String name, String type, String value) {
    this.name = name;
    this.type = type;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

  public Map<String, XMLNode> getChildren() {
    return children;
  }

  public Map<String, XMLAttribute> getAttributes() {
    return attributes;
  }
}
