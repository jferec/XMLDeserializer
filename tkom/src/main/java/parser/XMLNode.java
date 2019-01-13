package parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class XMLNode {

  private String name;
  private List<String> values = new ArrayList<>();
  private LinkedHashMap<String, XMLNode> children = new LinkedHashMap<>();
  private Map<String, XMLAttribute> attributes = new LinkedHashMap<>();
  private boolean isSelfClosing = false;

  public String getName() {
    return name;
  }

  public List<String> getValues() {
    return values;
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

  public XMLNode addValue(String value) {
    this.values.add(value);
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
