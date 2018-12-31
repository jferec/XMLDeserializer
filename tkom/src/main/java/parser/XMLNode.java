package parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLNode {

  private final String name;
  private final String value;
  private Map<String, XMLNode> children = new HashMap<>();
  private Map<String, XMLAttribute> attributes = new HashMap<>();

  XMLNode(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
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

  public void setAttributes(List<XMLAttribute> attributes){
    for(XMLAttribute attr : attributes){
      this.attributes.put(attr.getName(), attr);
    }
  }

  public void setChildren(List<XMLNode> children){
    for(XMLNode child : children){
      this.children.put(child.name, child);
    }
  }
}
