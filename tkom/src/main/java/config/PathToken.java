package config;

public class PathToken {

  private PathTokenType type;
  private int nodeIndex = -1;
  private int valueIndex = -1;
  private String nodeName;
  private String attributeName;

  public PathTokenType getType() {
    return type;
  }

  public PathToken setType(PathTokenType type) {
    this.type = type;
    return this;
  }

  public String getNodeName() {
    return nodeName;
  }

  public PathToken setNodeName(String nodeName) {
    this.nodeName = nodeName;
    return this;
  }

  public String getAttributeName() {
    return attributeName;
  }

  public PathToken setAttributeName(String attributeName) {
    this.attributeName = attributeName;
    return this;
  }

  public int getNodeIndex() {
    return nodeIndex;
  }

  public PathToken setNodeIndex(int nodeIndex) {
    this.nodeIndex = nodeIndex;
    return this;
  }

  public int getValueIndex() {
    return valueIndex;
  }

  public PathToken setValueIndex(int valueIndex) {
    this.valueIndex = valueIndex;
    return this;
  }
}
