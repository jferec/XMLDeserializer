package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.management.modelmbean.XMLParseException;
import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;

public class Parser {

  private final Lexer lexer;
  private XMLFile xmlFile = new XMLFile();
  private Token token = null;

  public Parser(Lexer lexer) {
    this.lexer = lexer;
  }

  public XMLFile run() throws IOException, XMLParseException {
    return parse();
  }

  private XMLFile parse() throws IOException, XMLParseException {
    parseProlog();
    xmlFile.setRoot(parseNodes());
    return xmlFile;
  }

  private void parseProlog() throws IOException, XMLParseException {
    getNextToken();
    if (!tokenTypeEquals(TokenType.PrologBegin)) {
      throw new XMLParseException("Prolog is missing.");
    }
    getNextToken();
    if (!tokenTypeEquals(TokenType.CharSequence) || !tokenValueEquals("xml")) {
      throw new XMLParseException("XML Prolog has invalid format");
    }
    getNextTokenAndSkipWhiteSpace();
    parseAttributes();
    if (!tokenTypeEquals(TokenType.PrologEnd)) {
      throw new XMLParseException("Prolog is missing closing '?>'");
    }
    getNextTokenAndSkipWhiteSpace();
  }

  private List<XMLAttribute> parseAttributes() throws IOException, XMLParseException {
    List<XMLAttribute> attributes = new ArrayList<>();
    while (tokenTypeEquals(TokenType.CharSequence)) {
      attributes.add(parseAttribute());
    }
    return attributes;
  }

  private XMLAttribute parseAttribute() throws IOException, XMLParseException {
    if (!tokenTypeEquals(TokenType.CharSequence)) {
      throw new XMLParseException(
          String.format("ObjectField name expected at %s", lexer.getPosition()));
    }
    String attributeName = token.getValue();
    getNextTokenAndSkipWhiteSpace();
    if (!tokenTypeEquals(TokenType.Equals)) {
      throw new XMLParseException(
          String.format("ObjectField %s has no \'=\' sign after it's name at %s",
              attributeName, lexer.getPosition()));
    }
    getNextTokenAndSkipWhiteSpace();
    if (!tokenTypeEquals(TokenType.String)) {
      throw new XMLParseException(
          String.format("ObjectField value expected after '=' sign at %s", lexer.getPosition()));
    }
    String attributeValue = token.getValue();
    getNextTokenAndSkipWhiteSpace();
    return new XMLAttribute(attributeName, attributeValue);
  }

  private XMLNode parseNodes() throws IOException, XMLParseException {
    XMLNode root = parseOpeningTag();
    parseNode(root);
    if (!tokenTypeEquals(TokenType.EOT)) {
      throw new XMLParseException(String.format("End of text expected at %s", lexer.getPosition()));
    }
    return root;
  }

  private XMLNode parseNode(XMLNode parent)
      throws IOException, XMLParseException {
    while (tokenTypeEquals(TokenType.OpeningTagBegin) || tokenTypeEquals(TokenType.CharSequence)) {
      if (tokenTypeEquals(TokenType.CharSequence)) {
        parent.addValue(parseValue());
      }
      if (tokenTypeEquals(TokenType.OpeningTagBegin)) {
        XMLNode curr = parseOpeningTag();
        parent.addChild(curr);
        if (!curr.isSelfClosing()) {
          parseNode(curr);
        }
      }
    }
    parseClosingTag(parent);
    return parent;
  }

  /**
   * Current token is '<'
   */
  private XMLNode parseOpeningTag() throws IOException, XMLParseException {
    getNextToken();
    if (!tokenTypeEquals(TokenType.CharSequence)) {
      throw new XMLParseException(
          String.format("Expected CharSequence after '<' at %s", lexer.getPosition()));
    }
    String nodeName = token.getValue();
    getNextTokenAndSkipWhiteSpace();
    List<XMLAttribute> nodeAttributes = parseAttributes();
    XMLNode node = new XMLNode()
        .setName(nodeName)
        .setAttributes(nodeAttributes);
    switch (token.getType()) {
      case TagEnd:
        getNextTokenAndSkipWhiteSpace();
        return node;
      case SelfClosingTag:
        getNextTokenAndSkipWhiteSpace();
        return node.setSelfClosing(true);
      default:
        throw new XMLParseException("The opening tag is not closed properly");
    }
  }

  private String parseValue() throws IOException, XMLParseException {
    StringBuilder value = new StringBuilder();
    while (tokenTypeEquals(TokenType.CharSequence) || tokenTypeEquals(TokenType.WhiteSpace)) {
      value.append(token.getValue());
      getNextToken();
    }
    return value.toString().trim();
  }

  /**
   * Current token is '</'
   */
  private void parseClosingTag(XMLNode node) throws IOException, XMLParseException {
    getNextToken();
    if (!tokenTypeEquals(TokenType.CharSequence) || !tokenValueEquals(node.getName())) {
      throw new XMLParseException(
          String.format("Expected CharSequence with value matching the opening tag at %s",
              lexer.getPosition()));
    }
    getNextTokenAndSkipWhiteSpace();
    if (!tokenTypeEquals(TokenType.TagEnd)) {
      throw new XMLParseException("The closing tag is not closed properly.");
    }
    getNextTokenAndSkipWhiteSpace();
  }

  private boolean tokenTypeEquals(TokenType type) {
    return token.getType().equals(type);
  }

  private boolean tokenValueEquals(String value) {
    return token.getValue().equals(value);
  }

  private void skipWhiteSpace() throws IOException, XMLParseException {
    if (token != null && tokenTypeEquals(TokenType.WhiteSpace)) {
      getNextToken();
    }
  }

  private void getNextToken() throws IOException, XMLParseException {
    this.token = lexer.getNextToken();
  }

  private void getNextTokenAndSkipWhiteSpace() throws IOException, XMLParseException {
    getNextToken();
    skipWhiteSpace();
  }
}
