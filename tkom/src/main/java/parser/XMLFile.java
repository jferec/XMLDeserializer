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
}
