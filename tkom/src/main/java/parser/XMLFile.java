package parser;

import java.text.ParseException;

public class XMLFile {

  private XMLNode root;

  XMLFile() {
    this.root = null;
  }

  public void setRoot(XMLNode node) {
    XMLNode root = new XMLNode();
    root.addChild(node);
    this.root = root;
  }

  public XMLNode getRoot() {
    return root;
  }

  public String find(String path) throws ParseException {
    return root.find(path);
  }

}
