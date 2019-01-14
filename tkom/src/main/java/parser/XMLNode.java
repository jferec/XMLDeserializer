package parser;

import config.PathLexer;
import config.PathToken;
import config.PathTokenType;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class XMLNode {

  private String name;
  private List<String> values = new ArrayList<>();
  private LinkedHashMap<String, ArrayList<XMLNode>> children = new LinkedHashMap<>();
  private Map<String, XMLAttribute> attributes = new HashMap<>();
  private boolean isSelfClosing = false;

  public String getName() {
    return name;
  }

  public List<String> getValues() {
    return values;
  }

  public LinkedHashMap<String, ArrayList<XMLNode>> getChildren() {
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
    ArrayList<XMLNode> nodes = children.getOrDefault(child.name, new ArrayList<>());
    nodes.add(child);
    children.put(child.name, nodes);
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

  public String find(String path) throws ParseException {
    PathLexer lexer = new PathLexer(path).tokenize();
    PathToken curr = lexer.getNextToken();
    XMLNode visitor = this;
    while (curr != null && curr.getType().equals(PathTokenType.Node)) {
      XMLNode xmlNode = visitor
          .getChildren()
          .get(curr.getNodeName())
          .get(curr.getNodeIndex());
      visitor = xmlNode;
      curr = lexer.getNextToken();
    }
    if (curr == null) {
      throw new ParseException("Value or Attribute expected at the end", 0);
    }
    if (curr.getType().equals(PathTokenType.Attribute)) {
      XMLNode xmlNode = visitor
          .getChildren()
          .get(curr.getNodeName())
          .get(curr.getNodeIndex());
      if (lexer.isEmpty()) {
        return xmlNode
            .getAttributes()
            .get(curr.getAttributeName())
            .getValue();
      }
    } else {
      XMLNode xmlNode = visitor
          .getChildren()
          .get(curr.getNodeName())
          .get(curr.getNodeIndex());
      if (lexer.isEmpty()) {
        return xmlNode
            .getValues()
            .get(curr.getValueIndex());
      }
    }
    throw new ParseException("Path end expected", 0);
  }

  protected static int findIndex(String edge, char open, char close) {
    final int start = edge.indexOf(open);
    final int end = edge.indexOf(close);
    if (start < 0 && end < 0) {
      return 0;
    }
    if (start < 0 || end < 0 || end < start) {
      return -1;
    }
    try {
      return Integer.valueOf(edge.substring(start + 1, end));
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  protected static String findName(String edge, char open) {
    final int end = edge.indexOf(open);
    if (end < 0) {
      return edge;
    }
    return edge.substring(0, end);
  }
}
