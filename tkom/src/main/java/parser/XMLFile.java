package parser;

public class XMLFile {

  private XMLNode root;

  XMLFile() {
    this.root = null;
  }

  public void setRoot(XMLNode root) {
    this.root = root;
  }

  public XMLNode getRoot() {
    return root;
  }

  public XMLNode forPath(String path) {
    XMLNode visitor = root;
    String[] edges = path.split("\\.");
    if (!root.getName().equals(edges[0])) {
      return null;
    }
    for (int i = 1; i < edges.length; i++) {
      visitor = visitor.getChildren().get(i);
      if (visitor == null) {
        return null;
      }
    }
    return visitor;
  }
}
