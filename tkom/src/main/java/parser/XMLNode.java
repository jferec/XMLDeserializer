package parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLNode {

  private String name;
  private String value;
  private Map<String, XMLNode> children = new HashMap<>();
  private Map<String, XMLAttribute> attributes = new HashMap<>();
  private boolean isSelfClosing = false;

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

  public XMLNode setAttributes(List<XMLAttribute> attributes) {
    for (XMLAttribute attr : attributes) {
      this.attributes.put(attr.getName(), attr);
    }
    return this;
  }

  public void addChild(XMLNode child) {
    this.children.put(child.name, child);
  }

  public XMLNode setName(String name) {
    this.name = name;
    return this;
  }

  public XMLNode setValue(String value) {
    this.value = value;
    return this;
  }

  public boolean isSelfClosing() {
    return isSelfClosing;
  }

  public XMLNode setSelfClosing(boolean selfClosing) {
    isSelfClosing = selfClosing;
    return this;
  }
}
