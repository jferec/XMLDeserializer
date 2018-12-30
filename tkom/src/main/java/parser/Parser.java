package parser;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import javax.management.modelmbean.XMLParseException;
import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;

public class Parser {

  private static int MAX_SPECIAL_CHAR_LENGTH = 5;
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
    while (token.getType().equals(TokenType.Char)) {
      charSequence.append(token.getValue());
      getNextToken();
    }
    return charSequence.toString();
  }

  private String parseSpecialChar() throws IOException {
    StringBuilder specialChar = new StringBuilder();
    while (specialChar.length() < 5 && !token.getValue().equals(';')) {
      switch (specialChar.toString()) {

      }
    }
    getNextToken();
    specialChar.append(token.getValue());
    //todo
    return null;
  }


  private void skipWhiteScapes() throws IOException {
    lexer.skipWhiteSpaces();
  }

  private void getNextToken() throws IOException {
    this.token = lexer.getNextToken();
  }

  private void parseProlog() throws IOException, XMLParseException {
    Token curr = lexer.getNextToken();
    if (curr == null || curr.getType() != TokenType.PrologBegin) {
      throw new XMLParseException("Prolog is required.");
    }
    skipWhiteScapes();
    String xml = parseCharSequence();
    if (!xml.equals("xml")) {
      throw new XMLParseException("XML Prolog has invalid format");
    }
    skipWhiteScapes();


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
    String attributeValue = parseCharSequence();
    //todo
    return new XMLAttribute(attributeName, attributeValue);
  }
}
