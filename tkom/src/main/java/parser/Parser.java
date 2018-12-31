package parser;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.management.modelmbean.XMLParseException;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;

public class Parser {

  private static int MAX_SPECIAL_CHAR_LENGTH = 6;
  private static int MIN_SPECIAL_CHAR_LENGTH = 4;
  private static ImmutableMap<String, Character> SPECIAL_CHARS = ImmutableMap.<String, Character>builder()
      .put("&amp;", '&')
      .put("&lt;", '<')
      .put("&gt;", '>')
      .put("&quot;", '"')
      .put("&apos", '\'')
      .build();

  private final Lexer lexer;
  private XMLFile xmlFile = new XMLFile();
  private Token token = null;

  Parser(Lexer lexer) {
    this.lexer = lexer;
  }

  public void parseFile() {

  }

  private boolean tokenTypeEquals(TokenType type) {
    return token.getType().equals(type);
  }

  private String parseCharSequence() throws IOException, XMLParseException {
    skipWhiteScapes();
    StringBuilder charSequence = new StringBuilder();
    getNextToken();
    while (tokenTypeEquals(TokenType.Char)) {
      charSequence.append(token.getValue());
      getNextToken();
    }
    return charSequence.toString();
  }

  private String parseCharSequenceWithWhiteSpaces() throws IOException, XMLParseException {
    skipWhiteScapes();
    StringBuilder charSequence = new StringBuilder();
    getNextToken();
    while (tokenTypeEquals(TokenType.Char) || tokenTypeEquals(TokenType.WhiteSpace)) {
      char curr = token.getValue();
      if (curr == '&') {
        charSequence.append(parseSpecialChar());
      } else {
        charSequence.append(token.getValue());
      }
      getNextToken();
    }
    return charSequence.toString().trim();
  }

  /**
   * Called when we encounter '&' in char sequence (beginning of a special character)
   */
  private Character parseSpecialChar() throws IOException, XMLParseException {
    StringBuilder specialChar = new StringBuilder(token.getValue());
    while (specialChar.length() < MAX_SPECIAL_CHAR_LENGTH && !token.getValue().equals(';')) {
      getNextToken();
      specialChar.append(token.getValue());
      if (specialChar.length() >= MIN_SPECIAL_CHAR_LENGTH && SPECIAL_CHARS
          .containsKey(specialChar.toString())) {
        return SPECIAL_CHARS.get(specialChar.toString());
      }
    }
    throw new XMLParseException("Failed to parse the special char.");
  }


  private void skipWhiteScapes() throws IOException {
    lexer.skipWhiteSpaces();
  }

  private void getNextToken() throws IOException {
    this.token = lexer.getNextToken();
  }

  private void parseProlog() throws IOException, XMLParseException {
    getNextToken();
    if (!tokenTypeEquals(TokenType.PrologBegin)) {
      throw new XMLParseException("Prolog is missing.");
    }
    skipWhiteScapes();
    String xml = parseCharSequence();
    if (!xml.equals("xml")) {
      throw new XMLParseException("XML Prolog has invalid format");
    }
    parseAttributes();
    if(tokenTypeEquals(TokenType.PrologEnd)){
      throw new XMLParseException("Prolog is missing closing '?>'");
    }
    getNextToken();
  }

  private List<XMLAttribute> parseAttributes() throws IOException, XMLParseException {
    skipWhiteScapes();
    List<XMLAttribute> attributes = new ArrayList<>();
    while (tokenTypeEquals(TokenType.Char)) {
      attributes.add(parseAttribute());
    }
    return attributes;
  }

  private XMLNode parseNode() throws IOException, XMLParseException {
    getNextToken();
    Token curr = token;
    if(!curr.getType().equals(TokenType.OpeningTagBegin)){
      throw new XMLParseException("Opening Tag expected.");
    }
    String elementName = parseCharSequence();
    skipWhiteScapes();
    List<XMLAttribute> attributes = parseAttributes();
    getNextToken();
    if(tokenTypeEquals(TokenType.SelfClosingTag)){
      getNextToken();
      skipWhiteScapes();
      XMLNode node = new XMLNode(elementName, null);
      node.setAttributes(attributes);
      return node;
    }
    if(tokenTypeEquals(TokenType.TagEnd)){

    }
    //todo
    return null;
  }


  private XMLAttribute parseAttribute() throws IOException, XMLParseException {
    String attributeName = parseCharSequence();
    skipWhiteScapes();
    getNextToken();
    if (!token.getType().equals(TokenType.Equals)) {
      throw new XMLParseException(
          String.format("Attribute %s has no \'=\' sign after it's name", attributeName));
    }
    skipWhiteScapes();
    getNextToken();
    boolean singleQuotationMark;
    if (tokenTypeEquals(TokenType.SingleQuotationMark)) {
      singleQuotationMark = true;
    } else if (tokenTypeEquals(TokenType.DoubleQuotationMark)) {
      singleQuotationMark = false;
    } else {
      throw new XMLParseException(
          String
              .format("Attribute %s has no quotation mark sign after  \'=\' sign", attributeName));
    }
    skipWhiteScapes();
    String attributeValue = parseCharSequenceWithWhiteSpaces();
    getNextToken();
    if ((singleQuotationMark) ? !tokenTypeEquals(TokenType.SingleQuotationMark)
        : !tokenTypeEquals(TokenType.DoubleQuotationMark)) {
      throw new XMLParseException("Closing quotation mark is expected after attribute's value.");
    }
    skipWhiteScapes();
    return new XMLAttribute(attributeName, attributeValue);
  }
}
